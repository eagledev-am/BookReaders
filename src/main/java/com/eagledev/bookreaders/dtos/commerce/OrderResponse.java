package com.eagledev.bookreaders.dtos.commerce;

import com.eagledev.bookreaders.entities.enums.OrderStatus;
import com.eagledev.bookreaders.entities.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {
    private UUID uuid;
    private UUID userUuid;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private Set<OrderItemResponse> items;
}

