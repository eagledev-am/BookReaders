package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category,Integer> {
    Optional<Category> findByTag(String tag);
    boolean existsByTag(String tag);
    List<Category> findAllByTagIn(List<String> tags);
}
