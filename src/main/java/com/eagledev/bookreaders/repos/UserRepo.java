package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User , Integer> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
}
