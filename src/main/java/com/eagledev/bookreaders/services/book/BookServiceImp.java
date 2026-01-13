package com.eagledev.bookreaders.services.book;

import com.eagledev.bookreaders.dtos.book.BookAdminResponse;
import com.eagledev.bookreaders.dtos.book.BookRequest;
import com.eagledev.bookreaders.dtos.book.BookResponse;
import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.Category;
import com.eagledev.bookreaders.entities.DiscussionRoom;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.BookMapper;
import com.eagledev.bookreaders.repos.BookRepo;
import com.eagledev.bookreaders.services.author.AuthorService;
import com.eagledev.bookreaders.services.cateogry.CategoryService;
import com.eagledev.bookreaders.services.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImp implements BookService{

    private final FileStorageService fileStorageService;
    private final BookRepo bookRepo;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookMapper bookMapper;


    @Override
    public BookResponse getBook(UUID bookId) {
        Book book = getBookByUUid(bookId);
        if(book.isDeleted()){
            throw new ResourceNotFoundException("Book", "id",bookId);
        }
        return bookMapper.bookToBookResponse(
            book
        );
    }

    @Override
    public BookAdminResponse getBookForAdmin(UUID bookId) {
        return bookMapper.bookToBookAdminResponse(
                getBookByUUid(bookId)
        );
    }

    @Override
    public Page<BookResponse> getAllBooks(String query, Pageable pageable) {
        Page<Book> page;
        if (query == null || query.isBlank()) {
            page = bookRepo.findAllByDeletedFalse(pageable);
        } else {
            page = bookRepo.searchUnDeletedBooks(query, pageable);
        }
        return page.map(
                bookMapper::bookToBookResponse
        );
    }

    @Override
    public Page<BookAdminResponse> getAllBooksForAdmin(String query, Pageable pageable) {
        Page<Book> page;
        if (query == null || query.isBlank()) {
            page = bookRepo.findAll(pageable);
        } else {
            page = bookRepo.searchBooks(query, pageable);
        }
        return page.map(
                bookMapper::bookToBookAdminResponse
        );
    }

    @Transactional
    @Override
    public BookResponse createBook(BookRequest request, MultipartFile file) throws BadRequestException {
        if(bookRepo.existsByTitle(request.getTitle())){
            throw new BadRequestException("Book with this title already exists.");
        }


        List<Author> authors = getAuthors(request.getAuthorIds());
        List<Category> categories = getCategories(request.getCategoryTag());

        if (categories.size() != request.getCategoryTag().size()) {
            throw new ResourceNotFoundException("Category", "tag", request.getCategoryTag());
        }

        String fileUrl = null;
        if(file != null && !file.isEmpty()){
            if(file.getContentType() == null || !FileStorageService.ALLOWED_IMAGE_TYPES.contains(file.getContentType())){
                throw new BadRequestException("Only JPG and PNG images are allowed");
            }
            fileUrl = fileStorageService.uploadFile(FileStorageService.BOOK,file);
        }

        DiscussionRoom discussionRoom = DiscussionRoom.builder()
                .build();

        Book book = Book.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .edition(request.getEdition())
                .numberOfPages(request.getNumberOfPages())
                .price(request.getPrice())
                .language(request.getLanguage())
                .publishDate(request.getPublishDate())
                .ebookCoverUrl(fileUrl)
                .authors(authors)
                .categories(categories)
                .discussionRoom(discussionRoom)
                .build();
        discussionRoom.setBook(book);
        return bookMapper.bookToBookResponse(
                bookRepo.save(book)
        );
    }

    @Transactional
    @Override
    public BookResponse updateBook(UUID bookId, BookRequest request) {
        Book book = getBookByUUid(bookId);

        List<Author> authors = getAuthors(request.getAuthorIds());
        List<Category> categories = getCategories(request.getCategoryTag());

        if (categories.size() != request.getCategoryTag().size()) {
            throw new ResourceNotFoundException("Category", "tag", request.getCategoryTag());
        }


        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setEdition(request.getEdition());
        book.setNumberOfPages(request.getNumberOfPages());
        book.setPrice(request.getPrice());
        book.setLanguage(request.getLanguage());
        book.setPublishDate(request.getPublishDate());
        book.setAuthors(authors);
        book.setCategories(categories);

        return bookMapper.bookToBookResponse(
                bookRepo.save(book)
        );
    }

    @Override
    public void deleteBook(UUID bookId) {
        Book book = getBookByUUid(bookId);

        book.setDeleted(true);
        book.setDeletedAt(LocalDate.now());
        bookRepo.save(book);

        log.info("Book {} soft-deleted successfully at {}", bookId, LocalDate.now());
    }

    @Override
    public void restoreBook(UUID bookId) throws BadRequestException {
        Book book = getBookByUUid(bookId);

        if(!book.isDeleted()){
            throw new BadRequestException("Book is not deleted");
        }

        book.setDeleted(false);
        book.setDeletedAt(null);
        bookRepo.save(book);

        log.info("Book {} restored successfully", bookId);
    }

    @Override
    public void hardDeleteBook(UUID bookId) {
        Book book = getBookByUUid(bookId);
        bookRepo.delete(book);
        log.warn("HARD DELETE: Book {} permanently deleted", bookId);
    }

    @Override
    public void updateBookPhoto(UUID uuid, MultipartFile file) throws BadRequestException {
        if(file.getContentType() == null || !FileStorageService.ALLOWED_IMAGE_TYPES.contains(file.getContentType())){
            throw new BadRequestException("Only JPG and PNG images are allowed");
        }

        Book book = getBookByUUid(uuid);

        if(book.getEbookCoverUrl() != null && !book.getEbookCoverUrl().isBlank()){
            try {
                String url = book.getEbookCoverUrl();
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                fileStorageService.deleteFile(FileStorageService.BOOK, fileName);
            } catch (Exception e) {
                log.error("Warning: Could not delete old book image: " + e.getMessage());
            }
        }

        String fileUrl = fileStorageService.uploadFile(FileStorageService.BOOK,file);
        book.setEbookCoverUrl(fileUrl);
        bookRepo.save(book);
    }

    private List<Author> getAuthors(List<UUID> authorIds){
        return authorIds.stream()
                .map(authorService::getAuthorById)
                .toList();
    }


    private List<Category> getCategories(List<String> categoryTag) {
        return categoryService.getCategories(categoryTag);
    }

    Book getBookByUUid(UUID uUid){
        return bookRepo.findByUuid(uUid).orElseThrow(
                () -> new ResourceNotFoundException("Book","id",uUid)
        );
    }
}
