package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.room.PostResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/posts")
@RequiredArgsConstructor
@Tag(name = "Admin - Posts", description = "Admin post moderation")
public class AdminPostController {

    private final PostService postService;

    @Operation(summary = "List posts (admin)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostResponse> posts = postService.searchPosts(q, pageable);
        return ResponseEntity.ok(ApiResponseBuilder.success("Posts retrieved successfully", posts));
    }

    @Operation(summary = "Delete post (admin)")
    @DeleteMapping("/{postUuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID postUuid,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        postService.deletePost(postUuid, user.getUuid());
        return ResponseEntity.ok(ApiResponseBuilder.success("Post deleted successfully", null));
    }
}

