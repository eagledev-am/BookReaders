package com.eagledev.bookreaders.dtos.commerce;

import com.eagledev.bookreaders.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CheckoutRequestDto {

    @NotNull(message = "Address UUID is required")
    private UUID addressUuid;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String promocode;
}

