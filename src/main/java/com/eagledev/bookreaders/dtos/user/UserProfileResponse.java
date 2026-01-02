package com.eagledev.bookreaders.dtos.user;

import com.eagledev.bookreaders.entities.UserContact;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
       @JsonProperty("id") UUID uuid ,
        String name ,
        String email ,
        String bio,
        String photoUrl,
        LocalDate dateOfBirth,
        List<UserContactDto> contacts
) {
}
