package com.eagledev.bookreaders.dtos.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryDto {

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "Catalog name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String tag;
}
