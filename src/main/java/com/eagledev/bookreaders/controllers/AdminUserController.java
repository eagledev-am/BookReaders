package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.admin.AdminUserRequest;
import com.eagledev.bookreaders.dtos.admin.AdminUserResponse;
import com.eagledev.bookreaders.dtos.admin.AdminUserUpdateRequest;
import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.services.user.UserService;
import com.eagledev.bookreaders.services.user.admin.UserAdminService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "Admin user management")
public class AdminUserController {

    private final UserAdminService userService;

    @Operation(summary = "List users (admin)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getUsers(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "joinedDate") Pageable pageable) {
        Page<AdminUserResponse> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(ApiResponseBuilder.success("Users retrieved successfully", users));
    }

    @Operation(summary = "update user (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(@PathVariable UUID id ,
                                                                     @Valid @RequestBody AdminUserUpdateRequest request) {
        AdminUserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseBuilder.success("User created successfully", updated));
    }

    @Operation(summary = "Create user (admin)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success("User created successfully", created));
    }
    
    @Operation(summary = "Block user (admin)")
    @PatchMapping("/{userUuid}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> blockUser(@PathVariable UUID userUuid) {
        AdminUserResponse updated = userService.setUserEnabled(userUuid, false);
        return ResponseEntity.ok(ApiResponseBuilder.success("User blocked successfully", updated));
    }

    @Operation(summary = "Unblock user (admin)")
    @PatchMapping("/{userUuid}/unblock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> unblockUser(@PathVariable UUID userUuid) {
        AdminUserResponse updated = userService.setUserEnabled(userUuid, true);
        return ResponseEntity.ok(ApiResponseBuilder.success("User unblocked successfully", updated));
    }

    @Operation(summary = "Delete user (admin)")
    @DeleteMapping("/{userUuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userUuid) {
        userService.deleteUser(userUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("User deleted successfully", null));
    }
}

