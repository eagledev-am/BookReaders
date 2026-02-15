package com.eagledev.bookreaders.dtos.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
