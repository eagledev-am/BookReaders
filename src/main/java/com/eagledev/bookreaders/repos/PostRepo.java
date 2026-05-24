package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PostRepo extends JpaRepository<Post,Integer> {

    @EntityGraph(attributePaths = {"user" , "comments"})
    Optional<Post> findByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"user" , "comments"})
    @Query("SELECT p FROM Post p WHERE p.room.uuid = :roomUuid ORDER BY p.createdAt DESC")
    Page<Post> findByRoomUuid(@Param("roomUuid") UUID roomUuid, Pageable pageable);

    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.uuid = :userUuid")
    boolean isLikedByUser(@Param("postId") int postId, @Param("userUuid") UUID userUuid);

    @Query("SELECT pl.post.uuid FROM PostLike pl " +
            "WHERE pl.user.uuid = :userUuid AND pl.post.uuid IN :postUuids")
    Set<UUID> findPostUuidsLikedByUser(@Param("userUuid") UUID userUuid,
                                       @Param("postUuids") List<UUID> postUuids);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT p FROM Post p
        JOIN p.user u
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.createdAt DESC
    """)
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);
}
