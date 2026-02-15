package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Post;
import com.eagledev.bookreaders.entities.PostLike;
import com.eagledev.bookreaders.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostLikeRepo extends JpaRepository<PostLike, Long> {

    boolean existsByUserAndPost(User user, Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END FROM PostLike pl WHERE pl.user.uuid = :userUuid AND pl.post.uuid = :postUuid")
    boolean existsByUserUuidAndPostUuid(@Param("userUuid") UUID userUuid, @Param("postUuid") UUID postUuid);

    @Query("SELECT pl FROM PostLike pl WHERE pl.user.uuid = :userUuid AND pl.post.uuid = :postUuid")
    Optional<PostLike> findByUserUuidAndPostUuid(@Param("userUuid") UUID userUuid, @Param("postUuid") UUID postUuid);
}

