package com.eagledev.bookreaders.services.auth;

import com.eagledev.bookreaders.dtos.auth.AuthRequest;
import com.eagledev.bookreaders.dtos.auth.AuthResponse;
import com.eagledev.bookreaders.dtos.auth.RegisterRequest;
import com.eagledev.bookreaders.dtos.auth.ResetPasswordRequest;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.VerificationCode;
import com.eagledev.bookreaders.entities.enums.CodeType;
import com.eagledev.bookreaders.entities.enums.MailType;
import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.exceptions.auth.AccountNotVerifiedException;
import com.eagledev.bookreaders.exceptions.auth.InvalidVerificationCodeException;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.exceptions.auth.UserAlreadyExistsException;
import com.eagledev.bookreaders.repos.UserRepo;
import com.eagledev.bookreaders.repos.VerificationCodeRepo;
import com.eagledev.bookreaders.services.user.EmailNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{

    private final UserRepo userRepo;
    private final VerificationCodeRepo verificationCodeRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailNotificationPublisher emailPublisher;

    @Override
    public String registerUser(RegisterRequest userDtoRequest) throws UserAlreadyExistsException {
        if(userRepo.existsByEmail(userDtoRequest.getEmail())){
            throw new UserAlreadyExistsException("Email Already registered.");
        }
        User user = User.builder()
                .name(userDtoRequest.getName())
                .email(userDtoRequest.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(userDtoRequest.getPassword()))
                .enabled(false)
                .build();
        userRepo.save(user);

        String code = generateCode(user,CodeType.ACCOUNT_ACTIVATION);

        // Send email
        emailPublisher.sendEmailNotification(
                EmailNotification.builder()
                        .to(user.getEmail())
                        .subject("Verification Email")
                        .name(user.getName())
                        .code(code)
                        .Type(MailType.ACTIVATION_MAIL)
                        .build()
        );

        return "Verification code sent to your email.";
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       request.getEmail(),
                       request.getPassword()
               )
        );
        User user = userRepo.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if(!user.isEnabled()){
            throw new AccountNotVerifiedException("Account is disabled. Please verify your email first.");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public String verifyUser(String email, String code) {
        User user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User","email",email));
        VerificationCode verificationCode = verificationCodeRepo.findByUserAndCodeAndType(user,code,CodeType.ACCOUNT_ACTIVATION)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code"));

        if(verificationCode.isExpired()){
            throw new InvalidVerificationCodeException("Code expired. Please request a new one.");
        }

        user.setEnabled(true);
        userRepo.save(user);
        verificationCodeRepo.delete(verificationCode);

        return "Account Verified Successfully.";
    }

    @Override
    public String sendVerificationCode(String email) {
        User user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User","email",email));

        CodeType codeType = user.isEnabled()? CodeType.PASSWORD_RESET : CodeType.ACCOUNT_ACTIVATION;
        String code = generateCode(user,codeType);

        // send an email
        emailPublisher.sendEmailNotification(
                EmailNotification.builder()
                        .to(user.getEmail())
                        .subject("Verification Email")
                        .name(user.getName())
                        .code(code)
                        .Type(user.isEnabled()? MailType.RESET_PASSWORD_MAIL : MailType.ACTIVATION_MAIL)
                        .build()
        );

        return "Verification code sent to your email.";
    }

    @Override
    public String resetPassword(ResetPasswordRequest passwordRequest) throws BadRequestException {
        User user = userRepo.findUserByEmail(passwordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User","email",passwordRequest.getNewPassword()));
        VerificationCode verificationCode = verificationCodeRepo.findByUserAndCodeAndType(user,passwordRequest.getCode(),CodeType.PASSWORD_RESET)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid Verification Code"));

        if(verificationCode.isExpired()){
            throw new InvalidVerificationCodeException("Code expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepo.save(user);
        verificationCodeRepo.delete(verificationCode);

        return "Password reset successfully.";
    }


    private String generateCode(User user,CodeType type) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .user(user)
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // 15 Minute Expiration
                .build();

        verificationCodeRepo.save(verificationCode);
        return code;
    }


}
