package com.eagledev.bookreaders.services.user;

import com.eagledev.bookreaders.dtos.book.BookResponse;
import com.eagledev.bookreaders.entities.User;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.dtos.user.ChangePasswordRequest;
import com.eagledev.bookreaders.dtos.user.UserContactDto;
import com.eagledev.bookreaders.dtos.user.UserProfileRequest;
import com.eagledev.bookreaders.dtos.user.UserProfileResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserProfileResponse getUserProfile(UUID id);
    User getUserById(UUID id);
    UserProfileResponse updateUserProfile(UUID id , UserProfileRequest profileRequest);
    void changePassword(UUID id , ChangePasswordRequest passwordRequest);
    void followAuthor(UUID userId, UUID authorId) throws BadRequestException;
    void unFollowAuthor(UUID userId, UUID authorId) throws BadRequestException;
    String uploadProfilePhoto(UUID userId, MultipartFile file) throws BadRequestException;
    List<UserContactDto> addContact(UUID userId, UserContactDto contactDto);
    List<AuthorResponse> getFollowedAuthors(UUID userId);
    List<BookResponse> getReadBooks(UUID userId);
}
