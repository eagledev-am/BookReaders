package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.commerce.CartResponse;
import com.eagledev.bookreaders.services.cart.CartService;
import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commerce/cart")
@RequiredArgsConstructor
@Tag(name = "Commerce - Cart", description = "Manage cart items")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get cart")
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        CartResponse cart = cartService.getCartByUser(currentUserUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Cart retrieved successfully", cart));
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/items/{bookUuid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @PathVariable UUID bookUuid,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        CartResponse cart = cartService.addItemToCart(currentUserUuid, bookUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Item added to cart", cart));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{bookUuid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable UUID bookUuid,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        CartResponse cart = cartService.removeItemFromCart(currentUserUuid, bookUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Item removed from cart", cart));
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        cartService.clearCart(currentUserUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Cart cleared", null));
    }

    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}

