package com.eagledev.bookreaders.services.author;

import com.eagledev.bookreaders.dtos.author.AuthorAdminResponse;
import com.eagledev.bookreaders.dtos.author.AuthorRequest;
import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.AuthorMapper;
import com.eagledev.bookreaders.repos.AuthorRepo;
import com.eagledev.bookreaders.repos.UserAuthorRepo;
import com.eagledev.bookreaders.services.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService{

    private final AuthorRepo authorRepo;
    private final AuthorMapper mapper;
    private final FileStorageService fileStorageService;
    private final UserAuthorRepo userAuthorRepo;

    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAllAuthors(String query, Pageable pageable) {
        Page<Author> authors;
        if (query == null || query.isBlank()) {
            authors = authorRepo.findAllByDeletedFalse(pageable);
        } else {
            authors = authorRepo.findByNameContainingIgnoreCaseAndDeletedFalse(query, pageable);
        }
        return authors.map(
                mapper::authorToAuthorResponse
        );
    }

    @Transactional(readOnly = true)
    public Page<AuthorAdminResponse> getAllAuthorsForAdmin(String query, Pageable pageable) {
        Page<Author> authors;
        if (query == null || query.isBlank()) {
            authors = authorRepo.findAll(pageable);
        } else {
            authors = authorRepo.findByNameContainingIgnoreCase(query, pageable);
        }
        return authors.map(
                mapper::authorToAuthorAdminResponse
        );
    }

    @Override
    public Author getAuthorById(UUID authorId) {
        return authorRepo.findByUuid(authorId).orElseThrow(
                () -> new ResourceNotFoundException("Author","id",authorId)
        );
    }

    @Override
    public AuthorResponse getAuthor(UUID authorId) {
        Author author = getAuthorById(authorId);
        if(author.isDeleted()){
            throw new ResourceNotFoundException("Author", "id",authorId);
        }
        return mapper.authorToAuthorResponse(author);
    }

    @Override
    public AuthorAdminResponse getAuthorForAdmin(UUID authorId) {
        Author author = getAuthorById(authorId);
        return mapper.authorToAuthorAdminResponse(author);
    }

    @Transactional
    @Override
    public AuthorResponse createAuthor(AuthorRequest request , MultipartFile file) throws BadRequestException {
        if(authorRepo.existsByName(request.getName())){
            throw new BadRequestException("Author with this name already exists.");
        }

        String fileUrl = null;
        if(file != null && !file.isEmpty()){
            if(file.getContentType() == null || !FileStorageService.ALLOWED_IMAGE_TYPES.contains(file.getContentType())){
                throw new BadRequestException("Only JPG and PNG images are allowed");
            }
            fileUrl = fileStorageService.uploadFile(FileStorageService.AUTHOR,file);
        }

        Author author = Author.builder()
                .bio(request.getBio())
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .nationality(request.getNationality())
                .photoUrl(fileUrl)
                .build();

        return mapper.authorToAuthorResponse(
                authorRepo.save(author)
        );
    }

    @Override
    public AuthorResponse updateAuthor(UUID authorId, AuthorRequest request) {
        Author author = getAuthorById(authorId);

        author.setName(request.getName());
        author.setBio(request.getBio());
        author.setNationality(request.getNationality());
        author.setDateOfBirth(request.getDateOfBirth());

        return mapper.authorToAuthorResponse(
                authorRepo.save(author)
        );
    }

    @Override
    public void deleteAuthor(UUID authorId) {
        Author author = getAuthorById(authorId);

        author.setDeleted(true);
        author.setDeletedAt(LocalDateTime.now());
        authorRepo.save(author);

        log.info("Author {} soft-deleted successfully at {}", authorId, LocalDateTime.now());
    }

    @Override
    public void restoreAuthor(UUID authorId) throws BadRequestException {
        Author author = getAuthorById(authorId);

        if(!author.isDeleted()){
            throw new BadRequestException("Author is not deleted");
        }

        author.setDeleted(false);
        author.setDeletedAt(null);
        authorRepo.save(author);

        log.info("Author {} restored successfully", authorId);
    }

    @Override
    public void hardDeleteAuthor(UUID authorId) {
        Author author = getAuthorById(authorId);
        userAuthorRepo.deleteByAuthorId(author.getId());
        authorRepo.delete(author);
        log.warn("HARD DELETE: Author {} permanently deleted", authorId);
    }

    @Override
    public void updateAuthorPhoto(UUID uuid, MultipartFile file) throws BadRequestException {
        if(file.getContentType() == null || !FileStorageService.ALLOWED_IMAGE_TYPES.contains(file.getContentType())){
            throw new BadRequestException("Only JPG and PNG images are allowed");
        }

        Author author = getAuthorById(uuid);

        if(author.getPhotoUrl() != null && !author.getPhotoUrl().isBlank()){
            try {
                String url = author.getPhotoUrl();
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                fileStorageService.deleteFile(FileStorageService.AUTHOR, fileName);
            } catch (Exception e) {
                log.error("Warning: Could not delete old author image: " + e.getMessage());
            }
        }

        String fileUrl = fileStorageService.uploadFile(FileStorageService.AUTHOR,file);
        author.setPhotoUrl(fileUrl);
        authorRepo.save(author);
    }
}
