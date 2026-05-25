package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepo extends JpaRepository<Author,Integer> {
    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Optional<Author> findByUuid(UUID uuid);

    @Query(value = "SELECT COUNT(*) FROM book WHERE author_id = :authorId",
            nativeQuery = true)
    long countBooksByAuthorId(@Param("authorId") Integer authorId);

    @Query("select count(ua) from UserAuthor ua where ua.author.id = :authorId")
    long countFollowersByAuthorId(@Param("authorId") Integer authorId);

    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Page<Author> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);

    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Page<Author> findAllByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Optional<Author>findByNameContainingIgnoreCase(String name);

    @EntityGraph(attributePaths = {"books","books.discussionRoom"})
    Page<Author> findAll(Pageable pageable);

    
    boolean existsByName(String name);

}
