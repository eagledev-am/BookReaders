package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRatingRepo extends JpaRepository<UserRating, Integer> {


    @Query("""
            SELECT ur FROM UserRating ur
            WHERE ur.user.uuid = :userUuid AND ur.book.uuid = :bookUuid
            """)
    Optional<UserRating> findByUserUuidAndBookUuid(
            @Param("userUuid") UUID userUuid,
            @Param("bookUuid") UUID bookUuid
    );

}

