package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepo extends JpaRepository<Author,Integer> {
    Optional<Author> findByUuid(UUID uuid);
}
