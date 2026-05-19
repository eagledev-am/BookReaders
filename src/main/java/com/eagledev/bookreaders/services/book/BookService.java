package com.eagledev.bookreaders.services.book;


import com.eagledev.bookreaders.dtos.book.*;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BookService {
    BookResponse getBook(UUID bookId);
    BookAdminResponse getBookForAdmin(UUID bookId);
    Page<BookPageResponse> getAllBooks(String query, Pageable pageable);
    Page<BookPageAdminResponse> getAllBooksForAdmin(String query, Pageable pageable);
    BookResponse createBook(BookRequest request , MultipartFile file) throws BadRequestException;
    BookResponse updateBook(UUID bookId , BookRequest request);
    void deleteBook(UUID bookId);
    void restoreBook(UUID bookId) throws BadRequestException;
    void hardDeleteBook(UUID bookId);
    void updateBookPhoto(UUID uuid , MultipartFile photo) throws BadRequestException;
    boolean isExistedByUuid(UUID uuid);
}
