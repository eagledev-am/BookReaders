package com.eagledev.bookreaders.dtos.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class BookResponse {
    private UUID uuid;
    private String title;
    private String description;

    private String edition;
    private Integer numberOfPages;
    private BigDecimal price;
    private String language;
    private LocalDate publishDate;

    private String ebookCoverUrl;

    private List<BookAuthorResponse> authors;
    private List<String> categories;

    private Double averageRating;
    private UUID discussionRoomUuid;
}
