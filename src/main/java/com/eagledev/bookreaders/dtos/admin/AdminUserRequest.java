package com.eagledev.bookreaders.dtos.admin;

import com.eagledev.bookreaders.entities.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminUserRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String bio;

    @Past(message = "Date must be in the past")
    private LocalDate dateOfBirth;

    private String photoUrl;

    private Role role;

    private Boolean enabled;
}

