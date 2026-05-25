package com.eagledev.bookreaders.services.cart;

import com.eagledev.bookreaders.dtos.commerce.CartResponse;
import com.eagledev.bookreaders.entities.Cart;
import com.eagledev.bookreaders.entities.CartItem;
import com.eagledev.bookreaders.mappers.CartMapper;
import com.eagledev.bookreaders.repos.CartItemRepo;
import com.eagledev.bookreaders.repos.CartRepo;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.BookRepo;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final BookRepo bookRepo;
    private final UserService userService;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse addItemToCart(UUID userUuid, UUID bookUuid) {
        Cart cart = getOrCreateCart(userUuid);

        boolean alreadyInCart = cart.getItems().stream()
                .anyMatch(item -> item.getBook().getUuid().equals(bookUuid));

        if(!alreadyInCart){
            Book book = bookRepo.findByUuid(bookUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "uuid", bookUuid));
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .build();
            cart.getItems().add(cartItem);
            cartRepo.save(cart);
        }
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(UUID userUuid, UUID bookUuid) {
        Cart cart = getOrCreateCart(userUuid);
        cart.getItems().removeIf(item -> item.getBook().getUuid().equals(bookUuid));
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse getCartByUser(UUID userUuid) {
        Cart cart = cartRepo.findByUserUuid(userUuid)
                .orElseGet(() -> createCart(userUuid));
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(UUID userUuid) {
        Cart cart = getOrCreateCart(userUuid);
        cart.getItems().clear();
    }

    private Cart getOrCreateCart(UUID userUuid) {
        return cartRepo.findByUserUuid(userUuid)
                .orElseGet(() -> createCart(userUuid));
    }

    private Cart createCart(UUID userUuid) {
        User user = userService.getUserById(userUuid);
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return cartRepo.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        CartResponse response = cartMapper.cartToCartResponse(cart);
        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            response.setTotalItems(0);
            response.setTotalAmount(BigDecimal.ZERO);
            return response;
        }

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getBook().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalItems(items.size());
        response.setTotalAmount(totalAmount);
        return response;
    }
}

