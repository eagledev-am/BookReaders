package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.UserReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            SELECT bk FROM Book bk
            JOIN UserReadingProgress urp ON bk.id = urp.book.id
            WHERE urp.user.id = :userId
            """)
    List<Book> findByUserId(
            @Param("userId") int userId
    );
}

