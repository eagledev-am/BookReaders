package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.room.PostRequest;
import com.eagledev.bookreaders.dtos.room.PostResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Social - Posts", description = "Manage discussion posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Create a new post", description = "Adds a post to a specific discussion room.")
    @PostMapping("/rooms/{roomUuid}/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @PathVariable UUID roomUuid,
            @RequestBody @Valid PostRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        PostResponse post = postService.createPost(roomUuid, currentUserUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Post created successfully", post)
        );
    }

    @Operation(summary = "Get posts feed", description = "Paginated list of posts for a room.")
    @GetMapping("/rooms/{roomUuid}/posts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @PathVariable UUID roomUuid,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            Authentication authentication) {

        UUID currentUserUuid = (authentication != null) ? getUserId(authentication) : null;
        Page<PostResponse> posts = postService.getPosts(roomUuid, currentUserUuid, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Posts retrieved successfully", posts)
        );
    }

    @Operation(summary = "Get post details", description = "Returns a single post.")
    @GetMapping("/posts/{postUuid}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostDetails(
            @PathVariable UUID postUuid,
            Authentication authentication) {

        UUID currentUserUuid =  getUserId(authentication);
        PostResponse post = postService.getPostDetails(postUuid, currentUserUuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Post retrieved successfully", post)
        );
    }

    @Operation(summary = "Update a post", description = "Edit the content of an existing post.")
    @PutMapping("/posts/{postUuid}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable UUID postUuid,
            @RequestBody @Valid PostRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        PostResponse post = postService.updatePost(postUuid, currentUserUuid, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Post updated successfully", post)
        );
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping("/posts/{postUuid}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID postUuid,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        postService.deletePost(postUuid, currentUserUuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Post deleted successfully", null)
        );
    }

    private UUID getUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}