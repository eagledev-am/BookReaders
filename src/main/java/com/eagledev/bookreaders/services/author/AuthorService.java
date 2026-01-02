package com.eagledev.bookreaders.services.author;

import com.eagledev.bookreaders.entities.Author;

import java.util.UUID;

public interface AuthorService {
    Author getAuthorById(UUID authorId);
}
