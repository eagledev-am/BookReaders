package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.room.CommentRequest;
import com.eagledev.bookreaders.dtos.room.CommentResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.comment.CommentService;
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
@Tag(name = "Social - Comments", description = "Manage post comments and replies")
public class CommentController {

    private final CommentService commentService;


    @Operation(summary = "Add a comment to a post", description = "Creates a top-level comment on a specific post.")
    @PostMapping("/posts/{postUuid}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable UUID postUuid,
            @RequestBody @Valid CommentRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        CommentResponse comment = commentService.addComment(postUuid, currentUserUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Comment added successfully", comment)
        );
    }

    @Operation(summary = "Reply to a comment", description = "Creates a reply to an existing comment.")
    @PostMapping("/comments/{parentUuid}/replies")
    public ResponseEntity<ApiResponse<CommentResponse>> replyToComment(
            @PathVariable UUID parentUuid,
            @RequestBody @Valid CommentRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        CommentResponse reply = commentService.replyToComment(parentUuid, currentUserUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Reply added successfully", reply)
        );
    }

    @Operation(summary = "Get comments for a post", description = "Returns paginated top-level comments for a post. Replies can be fetched lazily.")
    @GetMapping("/posts/{postUuid}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable UUID postUuid,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<CommentResponse> comments = commentService.getCommentsByPost(postUuid, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Comments retrieved successfully", comments)
        );
    }

    @Operation(summary = "Update a comment", description = "Updates the text of an existing comment. User must be the owner.")
    @PutMapping("/comments/{commentUuid}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable UUID commentUuid,
            @RequestBody @Valid CommentRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        CommentResponse updatedComment = commentService.updateComment(commentUuid, currentUserUuid, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Comment updated successfully", updatedComment)
        );
    }

    @Operation(summary = "Delete a comment", description = "Deletes a comment. User must be the owner.")
    @DeleteMapping("/comments/{commentUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID commentUuid,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        commentService.deleteComment(commentUuid, currentUserUuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Comment deleted successfully", null)
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

