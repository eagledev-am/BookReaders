package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.post.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Social - Likes", description = "Manage post likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Toggle like on a post", description = "Likes the post if not already liked, unlikes if already liked.")
    @PostMapping("/posts/{postUuid}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            @PathVariable UUID postUuid,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        boolean isLiked = likeService.toggleLike(postUuid, currentUserUuid);
        String message = isLiked ? "Post liked successfully" : "Post unliked successfully";
        return ResponseEntity.ok(
                ApiResponseBuilder.success(message, isLiked)
        );
    }

    private UUID getUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Authentication required");
        }
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}

