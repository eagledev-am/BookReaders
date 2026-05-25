package com.eagledev.bookreaders.dtos.commerce;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartResponse {
    private UUID uuid;
    private UUID userUuid;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal totalAmount;
}

