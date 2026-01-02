package com.eagledev.bookreaders.dtos.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Setter
@Getter
public class AuthorResponse {
    @JsonProperty("id") private UUID uuid;

    private String name;

    private String bio;

    private LocalDate dateOfBirth;

    private String photoUrl;

    private String nationality;
}
