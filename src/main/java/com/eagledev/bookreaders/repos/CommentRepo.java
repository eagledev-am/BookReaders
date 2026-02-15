package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepo extends JpaRepository<Comment, Integer> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Comment> findByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Comment c WHERE c.post.uuid = :postUuid AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findTopLevelCommentsByPostUuid(@Param("postUuid") UUID postUuid, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent.uuid = :parentUuid")
    int countRepliesByParentUuid(@Param("parentUuid") UUID parentUuid);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.uuid = :postUuid")
    int countByPostUuid(@Param("postUuid") UUID postUuid);

    List<Comment> findByParentId(int parentId);
}
