package com.eagledev.bookreaders.services.user.admin;

import com.eagledev.bookreaders.dtos.admin.AdminUserRequest;
import com.eagledev.bookreaders.dtos.admin.AdminUserResponse;
import com.eagledev.bookreaders.dtos.admin.AdminUserUpdateRequest;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.UserMapper;
import com.eagledev.bookreaders.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImp implements UserAdminService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(userMapper::toAdminUserResponse);
    }

    @Override
    public Page<AdminUserResponse> searchUsers(String query, Pageable pageable) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isBlank()) {
            return getAllUsers(pageable);
        }
        return userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(normalized, normalized, pageable)
                .map(userMapper::toAdminUserResponse);
    }

    @Override
    @Transactional
    public AdminUserResponse createUser(AdminUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .dateOfBirth(request.getDateOfBirth())
                .photoUrl(request.getPhotoUrl())
                .role(request.getRole() == null ? Role.USER : request.getRole())
                .enabled(request.getEnabled() == null || request.getEnabled())
                .build();

        User saved = userRepo.save(user);
        return userMapper.toAdminUserResponse(saved);
    }

    @Override
    public AdminUserResponse updateUser(UUID userUuid, AdminUserUpdateRequest request) {
        User user = getUserById(userUuid);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhotoUrl(request.getPhotoUrl());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.getEnabled());
        User saved = userRepo.save(user);

        return userMapper.toAdminUserResponse(saved);
    }

    @Override
    @Transactional
    public AdminUserResponse setUserEnabled(UUID userUuid, boolean enabled) {
        User user = getUserById(userUuid);
        user.setEnabled(enabled);
        User saved = userRepo.save(user);
        return userMapper.toAdminUserResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userUuid) {
        User user = getUserById(userUuid);
        userRepo.delete(user);
    }

    private User getUserById(UUID id){
        return userRepo.findUserByUuid(id).orElseThrow(
                () -> new ResourceNotFoundException("User","id", id)
        );
    }
}
