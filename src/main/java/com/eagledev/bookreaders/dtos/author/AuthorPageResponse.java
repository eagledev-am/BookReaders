package com.eagledev.bookreaders.dtos.author;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorPageResponse(
        UUID uuid,
        String name,
        String bio,
        String photoUrl,
        String nationality,
        int followersCount,
        int booksCount
) {
}
