package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepo extends JpaRepository<Book,Integer> {
    @EntityGraph(attributePaths = {"authors","discussionRoom"})
    Optional<Book> findByUuid(UUID uuid);

    @Override
    @EntityGraph(attributePaths = {"discussionRoom" , "authors"})
    Page<Book> findAll(Pageable pageable);

    boolean existsByTitle(String title);

    @EntityGraph(attributePaths = {"authors","discussionRoom"})
    Page<Book> findAllByDeletedFalse(Pageable pageable);

    @Query("""
    SELECT DISTINCT b FROM Book b
    LEFT JOIN FETCH b.authors a
    LEFT JOIN FETCH b.discussionRoom d
    LEFT JOIN b.categories c
    WHERE
        LOWER(b.title)       LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(b.language) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(a.name)        LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(c.tag)         LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);

    @Query("""
    SELECT DISTINCT b FROM Book b
    LEFT JOIN FETCH b.authors a
    LEFT JOIN FETCH b.discussionRoom d
    LEFT JOIN b.categories c
    WHERE
        LOWER(b.title)       LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(b.language) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(a.name)        LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(c.tag)         LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Book> searchUnDeletedBooks(@Param("query") String query, Pageable pageable);

    boolean existsByUuid(UUID uuid);

    List<Book> findByUuidIn(List<UUID> uuids);
}