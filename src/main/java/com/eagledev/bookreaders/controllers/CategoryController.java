package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.book.CategoryDto;
import com.eagledev.bookreaders.services.cateogry.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage book categories/genres")
public class CategoryController {
    private final CategoryService catalogService;

    @Operation(summary = "List all categories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAll() {
        List<CategoryDto> catalogs = catalogService.getAllCatalogs();
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Catalogs retrieved successfully", catalogs)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new category")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> create(@Valid @RequestBody CategoryDto request) {
        CategoryDto catalog = catalogService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseBuilder.success("Catalog created successfully", catalog)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update catalog")
    @PutMapping("/{tag}")
    public ResponseEntity<ApiResponse<CategoryDto>> update(
            @PathVariable String tag,
            @Valid @RequestBody CategoryDto request) {
        CategoryDto catalog = catalogService.updateCategory(tag, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Catalog updated successfully", catalog)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete catalog")
    @DeleteMapping("/{tag}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String tag) {
        catalogService.deleteCatalog(tag);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Catalog deleted successfully", null)
        );
    }
}
