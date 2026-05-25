package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.user.AddressDto;
import com.eagledev.bookreaders.services.address.AddressService;
import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commerce/addresses")
@RequiredArgsConstructor
@Tag(name = "Commerce - Addresses", description = "Manage user addresses")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Add address")
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(
            @RequestBody @Valid AddressDto addressDto,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        AddressDto created = addressService.addAddress(currentUserUuid, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success("Address added successfully", created));
    }

    @Operation(summary = "Update address")
    @PutMapping("/{addressUuid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<AddressDto>> updateAddress(
            @PathVariable UUID addressUuid,
            @RequestBody @Valid AddressDto addressDto,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        AddressDto updated = addressService.updateAddress(addressUuid, currentUserUuid, addressDto);
        return ResponseEntity.ok(ApiResponseBuilder.success("Address updated successfully", updated));
    }

    @Operation(summary = "Get user addresses")
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<List<AddressDto>>> getUserAddresses(Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        List<AddressDto> addresses = addressService.getUserAddresses(currentUserUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Addresses retrieved successfully", addresses));
    }

    @Operation(summary = "Set default address")
    @PatchMapping("/{addressUuid}/default")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<AddressDto>> setDefaultAddress(
            @PathVariable UUID addressUuid,
            Authentication authentication) {
        UUID currentUserUuid = getUserId(authentication);
        AddressDto updated = addressService.setDefaultAddress(addressUuid, currentUserUuid);
        return ResponseEntity.ok(ApiResponseBuilder.success("Default address updated", updated));
    }

    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getUuid();
    }
}

