package com.eagledev.bookreaders.dtos.commerce;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PromocodeDto {
    private UUID uuid;

    @NotBlank(message = "Code is required")
    private String code;

    @Min(value = 1, message = "Discount must be at least 1%")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private int discountPercentage;

    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;

    private boolean active;
}

