package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.book.BookAdminResponse;
import com.eagledev.bookreaders.dtos.book.BookRequest;
import com.eagledev.bookreaders.dtos.book.BookResponse;
import com.eagledev.bookreaders.services.book.BookService;
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
@Tag(name = "Books", description = "Browse, search, and manage books")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Get Public Book List", description = "Search by title, description, or author. Returns active books only.")
    @GetMapping("/api/v1/books")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 12, sort = "title") Pageable pageable) {
        Page<BookResponse> books = bookService.getAllBooks(query, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Books retrieved successfully", books)
        );
    }

    @Operation(summary = "Get Public Book Details")
    @GetMapping("/api/v1/books/{uuid}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable UUID uuid) {
        BookResponse book = bookService.getBook(uuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book retrieved successfully", book)
        );
    }


    @Operation(summary = "Admin: Get All Books", description = "Includes deleted books and extra metadata.")
    @GetMapping("/api/v1/d/books")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BookAdminResponse>>> getAllBooksForAdmin(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 12, sort = "title") Pageable pageable) {
        Page<BookAdminResponse> books = bookService.getAllBooksForAdmin(query, pageable);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Books retrieved successfully (Admin view)", books)
        );
    }

    @Operation(summary = "Admin: Get Detailed Book View")
    @GetMapping("/api/v1/d/books/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookAdminResponse>> getBookForAdmin(@PathVariable UUID uuid) {
        BookAdminResponse book = bookService.getBookForAdmin(uuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book details retrieved successfully", book)
        );
    }

    @Operation(summary = "Admin: Create Book", description = "Upload JSON data and E-book cover together.")
    @PostMapping(value = "/api/v1/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("data") @Valid BookRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws BadRequestException {
        BookResponse book = bookService.createBook(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Book created successfully", book)
        );
    }

    @Operation(summary = "Admin: Update Book Info", description = "Updates text fields only. Use PATCH for photo/file.")
    @PutMapping("/api/v1/books/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable UUID uuid,
            @Valid @RequestBody BookRequest request) {
        BookResponse book = bookService.updateBook(uuid, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book updated successfully", book)
        );
    }

    @Operation(summary = "Admin: Update Book Cover Photo")
    @PatchMapping(value = "/api/v1/books/{uuid}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateBookPhoto(
            @PathVariable UUID uuid,
            @RequestPart("file") MultipartFile photo) throws BadRequestException {
        bookService.updateBookPhoto(uuid, photo);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book cover updated successfully", null)
        );
    }

    @Operation(summary = "Admin: Soft Delete Book")
    @DeleteMapping("/api/v1/books/{uuid}/soft")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable UUID uuid) {
        bookService.deleteBook(uuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book deleted successfully", null)
        );
    }

    @Operation(summary = "Admin: Restore Soft-Deleted Book")
    @PostMapping("/api/v1/books/{uuid}/restore")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> restoreBook(@PathVariable UUID uuid) throws BadRequestException {
        bookService.restoreBook(uuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book restored successfully", null)
        );
    }

    @Operation(summary = "Admin: Hard Delete Book", description = "Permanently removes the book and its files.")
    @DeleteMapping("/api/v1/books/{uuid}/hard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hardDeleteBook(@PathVariable UUID uuid) {
        bookService.hardDeleteBook(uuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Book permanently deleted", null)
        );
    }
}