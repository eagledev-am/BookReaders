package com.eagledev.bookreaders.dtos.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class AuthorBooksResponse {
    private UUID uuid;
    private String title;
    private String description;
    private String edition;
    private BigDecimal price;
    private String ebookCoverUrl;
}
