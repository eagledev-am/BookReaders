package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.UserReadingProgress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UserReadingProgressRepo extends JpaRepository<UserReadingProgress, Long> {

    @Query("""
            SELECT urp FROM UserReadingProgress urp
            WHERE urp.user.uuid = :userUuid AND urp.book.uuid = :bookUuid
            """)
    Optional<UserReadingProgress> findByUserUuidAndBookUuid(
            @Param("userUuid") UUID userUuid,
            @Param("bookUuid") UUID bookUuid
    );

    @Query("""
        SELECT DISTINCT urp.book FROM UserReadingProgress urp
        LEFT JOIN FETCH urp.book.discussionRoom
        WHERE urp.user.id = :userId
        AND urp.book.deleted = false
        """)
    List<Book> findByUserId(@Param("userId") int userId);

    @EntityGraph(attributePaths = {"book"})
    Set<UserReadingProgress> findByUserUuid(UUID userUuid);
}

