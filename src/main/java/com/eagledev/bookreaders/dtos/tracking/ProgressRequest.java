package com.eagledev.bookreaders.dtos.tracking;

import com.eagledev.bookreaders.entities.enums.ReadingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressRequest {

    @Min(value = 0, message = "Current page must be at least 0")
    private int currentPage;

    @NotNull(message = "Reading status is required")
    private ReadingStatus status;
}

