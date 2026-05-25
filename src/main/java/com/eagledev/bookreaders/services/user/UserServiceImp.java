package com.eagledev.bookreaders.services.user;

import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.dtos.book.BookResponse;
import com.eagledev.bookreaders.dtos.user.ChangePasswordRequest;
import com.eagledev.bookreaders.dtos.user.UserContactDto;
import com.eagledev.bookreaders.dtos.user.UserProfileRequest;
import com.eagledev.bookreaders.dtos.user.UserProfileResponse;
import com.eagledev.bookreaders.entities.*;
import com.eagledev.bookreaders.entities.enums.ContactType;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.AuthorMapper;
import com.eagledev.bookreaders.mappers.BookMapper;
import com.eagledev.bookreaders.mappers.UserMapper;
import com.eagledev.bookreaders.repos.UserAuthorRepo;
import com.eagledev.bookreaders.repos.UserContactRepo;
import com.eagledev.bookreaders.repos.UserReadingProgressRepo;
import com.eagledev.bookreaders.repos.UserRepo;
import com.eagledev.bookreaders.services.author.AuthorService;
import com.eagledev.bookreaders.services.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService{

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthorRepo userAuthorRepo;
    private final AuthorService authorService;
    private final UserContactRepo userContactRepo;
    private final AuthorMapper authorMapper;
    private final UserReadingProgressRepo readingProgressRepo;
    private final BookMapper bookMapper;
    private final FileStorageService fileStorageService;

    @Override
    public UserProfileResponse getUserProfile(UUID id) {
        User user = getUserById(id);
        return userMapper.toProfile(user);
    }

    @Override
    public User getUserById(UUID id){
        return userRepo.findUserByUuid(id).orElseThrow(
                () -> new ResourceNotFoundException("User","id", id)
        );
    }


    @Transactional
    @Override
    public UserProfileResponse updateUserProfile(UUID id, UserProfileRequest profileRequest) {
        User user = getUserById(id);

        user.setBio(profileRequest.getBio());
        user.setDateOfBirth(profileRequest.getDateOfBirth());
        user.setName(user.getName());

        userRepo.save(user);

        return userMapper.toProfile(user);
    }

    @Transactional
    @Override
    public void changePassword(UUID id, ChangePasswordRequest passwordRequest) {
        User user = getUserById(id);

        if(!passwordEncoder.matches(passwordRequest.getCurrentPassword() , user.getPassword())){
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmationPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepo.save(user);
    }

    @Transactional
    @Override
    public void followAuthor(UUID userId, UUID authorId) throws BadRequestException {
        User user = getUserById(userId);
        Author author = authorService.getAuthorById(authorId);

        if(userAuthorRepo.existsByUserAndAuthor(user,author)){
            throw new BadRequestException("You already following this author.");
        }

        UserAuthor follow = UserAuthor.builder()
                .user(user)
                .author(author)
                .build();

        userAuthorRepo.save(follow);
    }

    @Transactional
    @Override
    public void unFollowAuthor(UUID userId, UUID authorId) throws BadRequestException {
        User user = getUserById(userId);
        Author author = authorService.getAuthorById(authorId);

        if(!userAuthorRepo.existsByUserAndAuthor(user,author)){
            throw new BadRequestException("You are not following this author.");
        }

        userAuthorRepo.deleteByUserAndAuthor(user,author);
    }

    @Transactional
    @Override
    public String uploadProfilePhoto(UUID userId, MultipartFile file) throws BadRequestException {
        if(file.getContentType() == null || !FileStorageService.ALLOWED_IMAGE_TYPES.contains(file.getContentType())){
            throw new BadRequestException("Only JPG and PNG images are allowed");
        }

        User user = getUserById(userId);

        if(user.getPhotoUrl() != null && !user.getPhotoUrl().isBlank()){
            try {
                String url = user.getPhotoUrl();
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                fileStorageService.deleteFile(FileStorageService.AVATAR, fileName);
            } catch (Exception e) {
                log.error("Warning: Could not delete old avatar: " + e.getMessage());
            }
        }

        String filePath = fileStorageService.uploadFile(FileStorageService.AVATAR,file);

        user.setPhotoUrl(filePath);
        userRepo.save(user);

        return filePath;
    }

    @Override
    public List<UserContactDto> addContact(UUID userId, UserContactDto contactDto) {
        User user = getUserById(userId);

        ContactType type = ContactType.valueOf(contactDto.getContactType().toUpperCase());

        userContactRepo.findUserContactByUserAndContactType(user, type)
                .ifPresent(userContactRepo::delete);

        UserContact contact = new UserContact();
        contact.setContactType(type);
        contact.setContactValue(contactDto.getContactValue());
        contact.setUser(user);

        userContactRepo.save(contact);

        return user.getContacts()
                .stream()
                .map(userMapper::toContactDto)
                .toList();
    }

    @Override
    public List<AuthorResponse> getFollowedAuthors(UUID userId) {
        User user = getUserById(userId);
        return userAuthorRepo.findByUserId(user.getId())
                .stream()
                .map(ua -> authorMapper.authorToAuthorResponse(ua.getAuthor()))
                .toList();
    }

    @Override
    public List<BookResponse> getReadBooks(UUID userId) {
        User user = getUserById(userId);
        List<Book> books = readingProgressRepo.findByUserId(user.getId());
        return books.stream()
                .map(bookMapper::bookToBookResponse)
                .toList();
    }

}
