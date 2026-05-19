package com.eagledev.bookreaders.dtos.book;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookPageAdminResponse(
        UUID uuid,
        String title,
        String description,
        String edition,
        Integer numberOfPages,
        BigDecimal price,
        String ebookCoverUrl,
        List<BookAuthorResponse> authors,
        boolean deleted,
        LocalDateTime deletedAt
) {
}
