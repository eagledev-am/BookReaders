package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.commerce.CheckoutRequestDto;
import com.eagledev.bookreaders.dtos.commerce.OrderItemResponse;
import com.eagledev.bookreaders.dtos.commerce.OrderResponse;
import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.services.order.OrderService;
import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commerce")
@RequiredArgsConstructor
@Tag(name = "Commerce - Orders", description = "Checkout and order management")
public class CommerceController {

    private final OrderService orderService;

    @Operation(summary = "List current user's orders")
    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userUuid = getUserId(authentication);
        Page<OrderResponse> orders = orderService.getUserOrders(userUuid, pageable);
        return ResponseEntity.ok(ApiResponseBuilder.success("Orders retrieved successfully", orders));
    }

    @Operation(summary = "Delete current user's order")
    @DeleteMapping("/orders/{orderUuid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteMyOrder(
            @PathVariable UUID orderUuid,
            Authentication authentication) {
        UUID userUuid = getUserId(authentication);
        orderService.deleteUserOrder(orderUuid, userUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Order deleted successfully", null));
    }

    @Operation(summary = "List all orders (admin)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponseBuilder.success("Orders retrieved successfully", orders));
    }

    @Operation(summary = "Get order items (owner or admin)")
    @GetMapping("/orders/{orderUuid}/items")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderItems(
            @PathVariable UUID orderUuid,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean isAdmin = user.getRole() == Role.ADMIN;
        List<OrderItemResponse> items = orderService.getOrderItems(orderUuid, user.getUuid(), isAdmin);
        return ResponseEntity.ok(ApiResponseBuilder.success("Order items retrieved successfully", items));
    }


    @Operation(summary = "Checkout cart")
    @PostMapping("/orders/checkout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestBody @Valid CheckoutRequestDto request,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        OrderResponse order = orderService.checkoutCart(currentUserUuid, request);
        return ResponseEntity.ok(ApiResponseBuilder.success("Checkout completed", order));
    }

    @Operation(summary = "Mark order as paid")
    @PatchMapping("/orders/{orderUuid}/mark-paid")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> markOrderAsPaid(@PathVariable String orderUuid) {
        OrderResponse order = orderService.markOrderAsPaid(orderUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Order marked as paid", order));
    }

    @Operation(summary = "Cancell Order")
    @PatchMapping("/orders/{orderUuid}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderUuid , Authentication authentication) {
        orderService.cancelOrder(getUserId(authentication), UUID.fromString(orderUuid));
        return ResponseEntity.ok().build();
    }

    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}

