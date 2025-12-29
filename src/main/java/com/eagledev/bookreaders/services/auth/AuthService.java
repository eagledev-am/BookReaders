package com.eagledev.bookreaders.services.auth;

import com.eagledev.bookreaders.dtos.auth.AuthRequest;
import com.eagledev.bookreaders.dtos.auth.AuthResponse;
import com.eagledev.bookreaders.dtos.auth.RegisterRequest;
import com.eagledev.bookreaders.dtos.auth.ResetPasswordRequest;
import com.eagledev.bookreaders.exceptions.auth.UserAlreadyExistsException;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    String registerUser(RegisterRequest userDtoRequest) throws UserAlreadyExistsException;
    String verifyUser(String email , String code);
    String sendVerificationCode(String email);
    String resetPassword(ResetPasswordRequest passwordRequest) throws BadRequestException;
    AuthResponse authenticate(AuthRequest request);
}
