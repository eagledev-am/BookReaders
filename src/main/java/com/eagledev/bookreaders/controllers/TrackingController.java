package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.tracking.ProgressRequest;
import com.eagledev.bookreaders.dtos.tracking.ProgressResponse;
import com.eagledev.bookreaders.dtos.tracking.RatingRequest;
import com.eagledev.bookreaders.dtos.tracking.RatingResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.tracking.BookRatingService;
import com.eagledev.bookreaders.services.tracking.ReadingProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books/{bookUuid}")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Track reading progress and rate books")
public class TrackingController {

    private final ReadingProgressService progressService;
    private final BookRatingService ratingService;


    @Operation(summary = "Update reading progress", description = "Update or create reading progress for the current user on a specific book.")
    @PutMapping("/progress")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateProgress(
            @PathVariable UUID bookUuid,
            @RequestBody @Valid ProgressRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        ProgressResponse response = progressService.updateProgress(bookUuid, currentUserUuid, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Progress updated successfully", response)
        );
    }

    @Operation(summary = "Get reading progress", description = "Get the current user's reading progress for a specific book.")
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<ProgressResponse>> getProgress(
            @PathVariable UUID bookUuid,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        ProgressResponse response = progressService.getProgress(bookUuid, currentUserUuid);

        if (response == null) {
            return ResponseEntity.ok(
                    ApiResponseBuilder.success("No progress found for this book", null)
            );
        }

        return ResponseEntity.ok(
                ApiResponseBuilder.success("Progress retrieved successfully", response)
        );
    }


    @Operation(summary = "Rate a book", description = "Add or update the current user's rating for a specific book (score 1-5).")
    @PutMapping("/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> rateBook(
            @PathVariable UUID bookUuid,
            @RequestBody @Valid RatingRequest request,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        RatingResponse response = ratingService.updateRating(bookUuid, currentUserUuid, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Rating submitted successfully", response)
        );
    }

    @Operation(summary = "Get user's rating", description = "Get the current user's rating for a specific book.")
    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> getRating(
            @PathVariable UUID bookUuid,
            Authentication authentication) {

        UUID currentUserUuid = getUserId(authentication);
        RatingResponse response = ratingService.getRating(bookUuid, currentUserUuid);

        if (response == null) {
            return ResponseEntity.ok(
                    ApiResponseBuilder.success("No rating found for this book", null)
            );
        }

        return ResponseEntity.ok(
                ApiResponseBuilder.success("Rating retrieved successfully", response)
        );
    }

    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}

