package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.user.ChangePasswordRequest;
import com.eagledev.bookreaders.dtos.user.UserContactDto;
import com.eagledev.bookreaders.dtos.user.UserProfileRequest;
import com.eagledev.bookreaders.dtos.user.UserProfileResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/u")
@Tag(name = "User Profile", description = "Endpoints for managing user profile, social links, and author subscriptions")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication auth) {
        UserProfileResponse profile = userService.getUserProfile(getUserId(auth));
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Profile retrieved successfully", profile)
        );
    }

    @Operation(summary = "Get specific user profile (Public view)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable UUID id) {
        UserProfileResponse profile = userService.getUserProfile(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Profile retrieved successfully", profile)
        );
    }

    @Operation(summary = "Update profile info")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication auth,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse profile = userService.updateUserProfile(getUserId(auth), request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Profile updated successfully", profile)
        );
    }

    @Operation(summary = "Change password")
    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(getUserId(auth), request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Password changed successfully", null)
        );
    }

    @Operation(summary = "Upload profile photo")
    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) throws BadRequestException {
        String photoUrl = userService.uploadProfilePhoto(getUserId(auth), file);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Photo uploaded successfully", photoUrl)
        );
    }

    @Operation(summary = "Follow an author")
    @PostMapping("/me/authors/{authorId}")
    public ResponseEntity<ApiResponse<Void>> followAuthor(
            Authentication auth,
            @PathVariable UUID authorId) throws BadRequestException {
        userService.followAuthor(getUserId(auth), authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Author followed successfully", null)
        );
    }

    @Operation(summary = "Unfollow an author")
    @DeleteMapping("/me/authors/{authorId}")
    public ResponseEntity<ApiResponse<Void>> unFollowAuthor(
            Authentication auth,
            @PathVariable UUID authorId) throws BadRequestException {
        userService.unFollowAuthor(getUserId(auth), authorId);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author unfollowed successfully", null)
        );
    }

    @Operation(summary = "Get list of authors the user follows")
    @GetMapping("/me/authors")
    public ResponseEntity<ApiResponse<List<?>>> getMyFollowedAuthors(Authentication auth) {
        List<?> authors = userService.getFollowedAuthors(getUserId(auth));
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Followed authors retrieved successfully", authors)
        );
    }

    @Operation(summary = "Get list of reading books")
    @GetMapping("/me/books")
    public ResponseEntity<ApiResponse<List<?>>> getReadingBooks(Authentication auth) {
        List<?> books = userService.getReadBooks(getUserId(auth));
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Reading Books retrieved successfully", books)
        );
    }

    @Operation(summary = "Add a social contact link")
    @PostMapping("/me/contacts")
    public ResponseEntity<ApiResponse<List<UserContactDto>>> addContact(
            Authentication auth,
            @Valid @RequestBody UserContactDto contactDto) {
        List<UserContactDto> contacts = userService.addContact(getUserId(auth), contactDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Contact added successfully", contacts)
        );
    }

    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}
