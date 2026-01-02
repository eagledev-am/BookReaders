package com.eagledev.bookreaders.dtos.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserProfileRequest {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "bio is required")
    private String bio;

    @NotNull(message = "data of birth is required")
    @Past(message = "Date must be in the past")
    private LocalDate dateOfBirth;
}
