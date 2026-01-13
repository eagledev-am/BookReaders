package com.eagledev.bookreaders.dtos.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BookAdminResponse{
    private UUID uuid;
    private String title;
    private String description;

    private String edition;
    private Integer numberOfPages;
    private BigDecimal price;
    private String language;
    private LocalDate publishDate;

    @JsonProperty("book-cover")
    private String ebookCoverUrl;

    private List<BookAuthorResponse> authors;
    private List<String> categories;

    private Double averageRating;
    private UUID discussionRoomUuid;
    private boolean deleted;
    private LocalDate deletedAt;

}
