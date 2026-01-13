package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.author.AuthorAdminResponse;
import com.eagledev.bookreaders.dtos.author.AuthorRequest;
import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.services.author.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Public and Admin endpoints for managing authors")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Get Public Author List", description = "Returns active authors only.")
    @GetMapping("/api/v1/authors")
    public ResponseEntity<ApiResponse<Page<AuthorResponse>>> getAllAuthors(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<AuthorResponse> authors = authorService.getAllAuthors(query, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Authors retrieved successfully", authors)
        );
    }

    @Operation(summary = "Get Public Author Details")
    @GetMapping("/api/v1/authors/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthor(@PathVariable UUID id) {
        AuthorResponse author = authorService.getAuthor(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author retrieved successfully", author)
        );
    }

    @Operation(summary = "Admin: Get All Authors", description = "Includes deleted/inactive authors.")
    @GetMapping("/api/v1/d/authors")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuthorAdminResponse>>> getAllAuthorsForAdmin(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<AuthorAdminResponse> authors = authorService.getAllAuthorsForAdmin(query, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Authors retrieved successfully (Admin view)", authors)
        );
    }

    @Operation(summary = "Admin: Get Detailed Author View")
    @GetMapping("/api/v1/d/authors/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AuthorAdminResponse>> getAuthorForAdmin(@PathVariable UUID id) {
        AuthorAdminResponse author = authorService.getAuthorForAdmin(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author details retrieved successfully", author)
        );
    }

    @Operation(summary = "Admin: Create Author", description = "Upload JSON data and Image file together.")
    @PostMapping(value = "/api/v1/authors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("data") @Valid AuthorRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws BadRequestException {
        AuthorResponse author = authorService.createAuthor(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Author created successfully", author)
        );
    }

    @Operation(summary = "Admin: Update Author Info", description = "Updates text fields only. Use PATCH for photo.")
    @PutMapping("/api/v1/authors/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @PathVariable UUID id,
            @Valid @RequestBody AuthorRequest request) {
        AuthorResponse author = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author updated successfully", author)
        );
    }

    @Operation(summary = "Admin: Update Author Photo")
    @PatchMapping(value = "/api/v1/authors/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateAuthorPhoto(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) throws BadRequestException {
        authorService.updateAuthorPhoto(id, file);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author photo updated successfully", null)
        );
    }

    @Operation(summary = "Admin: Soft Delete Author")
    @DeleteMapping("/api/v1/authors/{id}/soft")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author deleted successfully", null)
        );
    }

    @Operation(summary = "Admin: Hard Delete Author", description = "Permanently removes the author from DB.")
    @DeleteMapping("/api/v1/authors/{id}/hard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hardDeleteAuthor(@PathVariable UUID id) {
        authorService.hardDeleteAuthor(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author permanently deleted", null)
        );
    }

    @Operation(summary = "Admin: Restore Author", description = "Recovers a soft-deleted author.")
    @PostMapping("/api/v1/authors/{id}/restore")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> restoreAuthor(@PathVariable UUID id) throws BadRequestException {
        authorService.restoreAuthor(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Author restored successfully", null)
        );
    }
}
