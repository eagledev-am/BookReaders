package com.eagledev.bookreaders.services.post;

import com.eagledev.bookreaders.entities.Post;
import com.eagledev.bookreaders.entities.PostLike;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.PostLikeRepo;
import com.eagledev.bookreaders.repos.PostRepo;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepo postLikeRepo;
    private final PostRepo postRepo;
    private final UserService userService;

    @Override
    @Transactional
    public boolean toggleLike(UUID postUuid, UUID currentUserUuid) {
        Post post = postRepo.findByUuid(postUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "uuid", postUuid));

        User user = userService.getUserById(currentUserUuid);

        Optional<PostLike> existingLike = postLikeRepo.findByUserAndPost(user, post);

        boolean isLiked;
        if (existingLike.isPresent()) {
            postLikeRepo.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            isLiked = false;
        } else {
            PostLike newLike = PostLike.builder()
                    .user(user)
                    .post(post)
                    .build();
            postLikeRepo.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            isLiked = true;
        }

        postRepo.save(post);
        return isLiked;
    }
}

