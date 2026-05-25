package com.eagledev.bookreaders.services.cart;

import com.eagledev.bookreaders.dtos.commerce.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse addItemToCart(UUID userUuid, UUID bookUuid);
    CartResponse removeItemFromCart(UUID userUuid, UUID bookUuid);
    CartResponse getCartByUser(UUID userUuid);
    void clearCart(UUID userUuid);
}

