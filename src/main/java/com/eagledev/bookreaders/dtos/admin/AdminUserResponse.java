package com.eagledev.bookreaders.dtos.admin;

import com.eagledev.bookreaders.entities.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AdminUserResponse {
    private UUID uuid;
    private String name;
    private String email;
    private String bio;
    private String photoUrl;
    private LocalDate dateOfBirth;
    private LocalDate joinedDate;
    private Role role;
    private boolean enabled;
}

