package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User , Integer> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findUserById(int id);
    Optional<User> findUserByUuid(UUID id);
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable);
}
