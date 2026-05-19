package com.eagledev.bookreaders.services.author;

import com.eagledev.bookreaders.dtos.author.*;
import com.eagledev.bookreaders.entities.Author;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AuthorService {
    Author getAuthorById(UUID authorId);
    Page<AuthorPageResponse> getAllAuthors(String query, Pageable pageable);
    Page<AuthorPageAdminResponse> getAllAuthorsForAdmin(String query, Pageable pageable);
    AuthorResponse getAuthor(UUID authorId);
    AuthorAdminResponse getAuthorForAdmin(UUID authorId);
    AuthorResponse createAuthor(AuthorRequest request , MultipartFile multipartFile) throws BadRequestException;
    AuthorResponse updateAuthor(UUID authorId , AuthorRequest request);
    void deleteAuthor(UUID authorId);
    void restoreAuthor(UUID authorId) throws BadRequestException;
    void hardDeleteAuthor(UUID authorId);
    void updateAuthorPhoto(UUID uuid , MultipartFile photo) throws BadRequestException;
}
