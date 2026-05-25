package com.eagledev.bookreaders.services.order;

import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.services.payment.PaymentEventPublisher;
import com.eagledev.bookreaders.services.payment.PaymentService;
import com.eagledev.bookreaders.dtos.commerce.CheckoutRequestDto;
import com.eagledev.bookreaders.dtos.commerce.OrderResponse;
import com.eagledev.bookreaders.dtos.commerce.OrderItemResponse;
import com.eagledev.bookreaders.entities.enums.OrderStatus;
import com.eagledev.bookreaders.entities.enums.PaymentStatus;
import com.eagledev.bookreaders.dtos.events.PaymentCompletedEvent;
import com.eagledev.bookreaders.mappers.OrderMapper;
import com.eagledev.bookreaders.entities.*;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.*;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final AddressRepo addressRepo;
    private final OrderRepo orderRepo;
    private final PaymentTransactionRepo paymentTransactionRepo;
    private final PromocodeRepo promocodeRepo;
    private final BookRepo bookRepo;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    @Transactional
    public OrderResponse checkoutCart(UUID userUuid, CheckoutRequestDto request) {
        Cart cart = cartRepo.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userUuid", userUuid));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Address address = addressRepo.findByUuid(request.getAddressUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "uuid", request.getAddressUuid()));

        if (!address.getUser().getUuid().equals(userUuid)) {
            throw new AccessDeniedException("You are not the owner of this address");
        }

        Set<OrderItem> items = cart.getItems().stream()
                .map(
                        cartItem -> {
                            BigDecimal price = cartItem.getBook().getPrice();
                            return OrderItem.builder()
                                    .book(cartItem.getBook())
                                    .priceAtPurchase(price)
                                    .build();
                        }
                ).collect(Collectors.toCollection(LinkedHashSet::new));

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getPriceAtPurchase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmount = applyPromocode(totalAmount, request.getPromocode());

        Order order = Order.builder()
                .user(userService.getUserById(userUuid))
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .shippingStreet(address.getStreet())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .shippingZipCode(address.getZipCode())
                .totalAmount(totalAmount)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        orderRepo.save(order);

        cart.getItems().clear();

        paymentService.processPayment(order.getUuid());
        return orderMapper.orderToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse markOrderAsPaid(String orderUuid) {
        UUID uuid = UUID.fromString(orderUuid);
        Order order = orderRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "uuid", orderUuid));



        order.setStatus(OrderStatus.PAID);

        paymentTransactionRepo.findByOrderUuid(uuid)
                .ifPresent(transaction -> {transaction.setStatus(PaymentStatus.SUCCESS);});

        List<UUID> bookUuids = order.getItems()
                .stream()
                .map(item -> item.getBook().getUuid())
                .toList();

        paymentEventPublisher.publishPaymentCompletedEvent(PaymentCompletedEvent.builder()
                .orderUuid(order.getUuid())
                .userUuid(order.getUser().getUuid())
                .bookUuids(bookUuids)
                .totalAmount(order.getTotalAmount())
                .build());

        return orderMapper.orderToOrderResponse(order);
    }

    @Transactional
    @Override
    public void cancelOrder(UUID userUuid,UUID orderUuid) {
        Order order = orderRepo.findByUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "uuid", orderUuid));
        User user = userService.getUserById(userUuid);

        if (!order.getUser().getUuid().equals(userUuid) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to cancel this order");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepo.save(order);

        paymentEventPublisher.publishPaymentCancelledEvent(PaymentCompletedEvent.builder()
                .orderUuid(order.getUuid())
                .userUuid(order.getUser().getUuid())
                .totalAmount(order.getTotalAmount())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(orderMapper::orderToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(UUID userUuid, Pageable pageable) {
        return orderRepo.findAllByUserUuid(userUuid, pageable)
                .map(orderMapper::orderToOrderResponse);
    }

    @Override
    @Transactional
    public void deleteUserOrder(UUID orderUuid, UUID userUuid) {
        Order order = orderRepo.findByUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "uuid", orderUuid));

        if (!order.getUser().getUuid().equals(userUuid)) {
            throw new AccessDeniedException("You are not the owner of this order");
        }

        orderRepo.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(UUID orderUuid, UUID userUuid, boolean isAdmin) {
        Order order = orderRepo.findByUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "uuid", orderUuid));

        if (!isAdmin && !order.getUser().getUuid().equals(userUuid)) {
            throw new AccessDeniedException("You are not allowed to access this order");
        }

        return order.getItems()
                .stream()
                .map(orderMapper::orderItemToOrderItemResponse)
                .toList();
    }

    private BigDecimal applyPromocode(BigDecimal totalAmount, String promocode) {
        if(promocode == null || promocode.isBlank()) return totalAmount;

        Promocode code = promocodeRepo.findByCodeIgnoreCaseAndIsActiveTrue(promocode)
                .filter(c -> !c.getExpirationDate().isBefore(LocalDate.now()))
                .orElseThrow(() -> new ResourceNotFoundException("Promocode", "code", promocode));

        BigDecimal discount = totalAmount
                .multiply(BigDecimal.valueOf(code.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return totalAmount.subtract(discount);
    }
}
