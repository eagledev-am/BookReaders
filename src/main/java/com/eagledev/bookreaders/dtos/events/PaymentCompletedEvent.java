package com.eagledev.bookreaders.dtos.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record PaymentCompletedEvent(
        UUID orderUuid,
        UUID userUuid,
        List<UUID> bookUuids,
        BigDecimal totalAmount
) {
}

