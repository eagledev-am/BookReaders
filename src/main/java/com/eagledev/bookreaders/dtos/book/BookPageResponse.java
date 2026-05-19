package com.eagledev.bookreaders.dtos.book;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BookPageResponse(
        UUID uuid,
        String title,
        String description,
        String edition,
        Integer numberOfPages,
        BigDecimal price,
        String ebookCoverUrl,
        List<BookAuthorResponse> authors,
        Double averageRating
        ) {
}
