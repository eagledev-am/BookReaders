package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.commerce.PromocodeDto;
import com.eagledev.bookreaders.services.promocode.PromocodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commerce/promocodes")
@RequiredArgsConstructor
@Tag(name = "Commerce - Promocodes", description = "Manage promocodes")
public class PromocodeController {

    private final PromocodeService promocodeService;

    @Operation(summary = "List all promocodes")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<PromocodeDto>>> getAll(@RequestParam(required = false) String code ,
                                                                  @PageableDefault(
                                                                       size = 20 , sort = "expirationDate"
                                                               ) Pageable pageable) {
        Page<PromocodeDto> promocodes = promocodeService.searchByCode(code , pageable);
        return ResponseEntity.ok(ApiResponseBuilder.success("Promocodes retrieved successfully", promocodes));
    }

    @Operation(summary = "Get promocode by UUID")
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PromocodeDto>> getByUuid(@PathVariable UUID uuid) {
        PromocodeDto promocode = promocodeService.getByUuid(uuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Promocode retrieved successfully", promocode));
    }

    @Operation(summary = "Create promocode")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PromocodeDto>> create(@Valid @RequestBody PromocodeDto request) {
        PromocodeDto created = promocodeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success("Promocode created successfully", created));
    }

    @Operation(summary = "Update promocode")
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PromocodeDto>> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody PromocodeDto request) {
        PromocodeDto updated = promocodeService.update(uuid, request);
        return ResponseEntity.ok(ApiResponseBuilder.success("Promocode updated successfully", updated));
    }

    @Operation(summary = "Set promocode active status")
    @PatchMapping("/{uuid}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PromocodeDto>> setActive(
            @PathVariable UUID uuid,
            @RequestParam boolean active) {
        PromocodeDto updated = promocodeService.setActive(uuid, active);
        return ResponseEntity.ok(ApiResponseBuilder.success("Promocode status updated", updated));
    }

    @Operation(summary = "Delete promocode")
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID uuid) {
        promocodeService.delete(uuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Promocode deleted successfully", null));
    }
}
