package com.eagledev.bookreaders.dtos.commerce;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemResponse {
    private UUID uuid;
    private UUID bookUuid;
    private String bookTitle;
    private BigDecimal priceAtPurchase;
}

