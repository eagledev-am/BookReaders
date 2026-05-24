package com.eagledev.bookreaders.services.user.admin;

import com.eagledev.bookreaders.dtos.admin.AdminUserRequest;
import com.eagledev.bookreaders.dtos.admin.AdminUserResponse;
import com.eagledev.bookreaders.dtos.admin.AdminUserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserAdminService {
    Page<AdminUserResponse> getAllUsers(Pageable pageable);
    Page<AdminUserResponse> searchUsers(String query, Pageable pageable);
    AdminUserResponse createUser(AdminUserRequest request);
    AdminUserResponse updateUser(UUID userUuid, AdminUserUpdateRequest request);
    AdminUserResponse setUserEnabled(java.util.UUID userUuid, boolean enabled);
    void deleteUser(java.util.UUID userUuid);
}
