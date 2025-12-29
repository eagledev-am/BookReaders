package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.auth.AuthRequest;
import com.eagledev.bookreaders.dtos.auth.AuthResponse;
import com.eagledev.bookreaders.dtos.auth.RegisterRequest;
import com.eagledev.bookreaders.dtos.auth.ResetPasswordRequest;
import com.eagledev.bookreaders.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login, registration, and account recovery")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new account and sends a verification email.")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @Operation(summary = "Authenticate user", description = "Returns a JWT token if credentials are valid.")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(
            @Valid @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(summary = "Verify account", description = "Activates the account using the email code.")
    @PostMapping(name = "/verify" , params = {"email" , "code"})
    public ResponseEntity<String> verifyAccount(
            @Valid @Email @RequestParam String email , @RequestParam String code
    ) {
        return ResponseEntity.ok(authService.verifyUser(email,code));
    }

    @Operation(summary = "Resend verification code", description = "Resends the code for activation or password reset.")
    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(authService.sendVerificationCode(email));
    }

    @Operation(summary = "Reset password", description = "Updates password if the verification code is valid.")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) throws BadRequestException {
        return ResponseEntity.ok(authService.resetPassword(
            request
        ));
    }

}
