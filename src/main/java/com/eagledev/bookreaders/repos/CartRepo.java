package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.book", "user"})
    Optional<Cart> findByUserUuid(UUID userUuid);

    @EntityGraph(attributePaths = {"items", "items.book", "user"})
    Optional<Cart> findByUuid(UUID uuid);
}

