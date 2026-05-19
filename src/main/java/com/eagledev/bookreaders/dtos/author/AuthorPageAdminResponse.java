package com.eagledev.bookreaders.dtos.author;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthorPageAdminResponse(
        UUID uuid,
        String name,
        String bio,
        String photoUrl,
        String nationality,
        LocalDate dateOfBirth,
        int followersCount,
        int booksCount,
        boolean deleted,
        LocalDateTime deletedAt
) {
}
