package com.eagledev.bookreaders.dtos.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class AuthorRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Bio is required")
    private String bio;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String nationality;
}
