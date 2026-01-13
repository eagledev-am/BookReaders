package com.eagledev.bookreaders.dtos.book;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String edition;

    @Min(value = 1, message = "Pages must be at least 1")
    private Integer numberOfPages;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    private String language;

    @PastOrPresent
    private LocalDate publishDate;

    @NotEmpty(message = "Select at least one author")
    private List<UUID> authorIds;

    @NotEmpty(message = "Select at least one category")
    private List<String> categoryTag;
}