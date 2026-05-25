package com.eagledev.bookreaders.services.order;

import com.eagledev.bookreaders.dtos.commerce.CheckoutRequestDto;
import com.eagledev.bookreaders.dtos.commerce.OrderResponse;
import com.eagledev.bookreaders.dtos.commerce.OrderItemResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkoutCart(UUID userUuid, CheckoutRequestDto request);
    OrderResponse markOrderAsPaid(String orderUuid);
    void cancelOrder(UUID userUuid , UUID orderUuid);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    Page<OrderResponse> getUserOrders(UUID userUuid, Pageable pageable);
    void deleteUserOrder(UUID orderUuid, UUID userUuid);
    List<OrderItemResponse> getOrderItems(UUID orderUuid, UUID userUuid, boolean isAdmin);
}
