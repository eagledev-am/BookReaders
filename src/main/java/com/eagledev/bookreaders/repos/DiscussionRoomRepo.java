package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.DiscussionRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DiscussionRoomRepo extends JpaRepository<DiscussionRoom,Integer> {
    @EntityGraph(attributePaths = {"book"})
    DiscussionRoom findDiscussionRoomByBookUuid(UUID bookId);

    Optional<DiscussionRoom> findByUuid(UUID uuid);

    @Query("""
         SELECT COUNT(P) FROM Post P
          WHERE P.room.id = :roomId
    """)
    int countPostsByRoomId(@Param("roomId") Integer roomId);

    boolean existsByUuid(UUID uuid);
}
