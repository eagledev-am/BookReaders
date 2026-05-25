package com.eagledev.bookreaders.services.tracking.progress;

import com.eagledev.bookreaders.dtos.tracking.ProgressRequest;
import com.eagledev.bookreaders.dtos.tracking.ProgressResponse;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.UserReadingProgress;
import com.eagledev.bookreaders.entities.enums.ReadingStatus;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.TrackingMapper;
import com.eagledev.bookreaders.repos.BookRepo;
import com.eagledev.bookreaders.repos.UserReadingProgressRepo;
import com.eagledev.bookreaders.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final UserReadingProgressRepo readingProgressRepo;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final TrackingMapper trackingMapper;

    @Override
    @Transactional
    public ProgressResponse updateProgress(UUID bookUuid, UUID userUuid, ProgressRequest request) {
        int currentPage = request.getCurrentPage();

        Book book = bookRepo.findByUuid(bookUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "uuid", bookUuid));

        int totalPages = book.getNumberOfPages() != null ? book.getNumberOfPages() : 0;

        validateCurrentPage(currentPage, totalPages);

        ReadingStatus status = determineStatus(currentPage, totalPages, request.getStatus());
        LocalDateTime now = LocalDateTime.now();

        UserReadingProgress progress = readingProgressRepo
                .findByUserUuidAndBookUuid(userUuid, bookUuid)
                .map(existing -> {
                    existing.setCurrentPage(currentPage);
                    existing.setStatus(status);
                    existing.setLastReadAt(now);
                    return existing;
                })
                .orElseGet(() -> {
                    User user = userRepo.findUserByUuid(userUuid)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

                    return UserReadingProgress.builder()
                            .user(user)
                            .book(book)
                            .currentPage(currentPage)
                            .totalPages(totalPages)
                            .status(status)
                            .lastReadAt(now)
                            .build();
                });
        readingProgressRepo.save(progress);
        return trackingMapper.toProgressResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressResponse getProgress(UUID bookUuid, UUID userUuid) {
        if (!bookRepo.existsByUuid(bookUuid)) {
            throw new ResourceNotFoundException("Book", "uuid", bookUuid);
        }

        return readingProgressRepo.findByUserUuidAndBookUuid(userUuid, bookUuid)
                .map(trackingMapper::toProgressResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public void grantDigitalAccess(UUID userUuid, List<UUID> bookUuids) {
        User user = userRepo.findUserByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        List<Book> books = bookRepo.findByUuidIn(bookUuids);

        Set<UUID> existingBookUuids = readingProgressRepo.findByUserUuid(userUuid)
                .stream()
                .map(p -> p.getBook().getUuid())
                .collect(Collectors.toSet());

        List<UserReadingProgress> newRecords = books.stream()
                .filter(book -> !existingBookUuids.contains(book.getUuid()))
                .map(book -> UserReadingProgress.builder()
                        .user(user)
                        .book(book)
                        .status(ReadingStatus.WANT_TO_READ)
                        .currentPage(0)
                        .totalPages(book.getNumberOfPages() != null ? book.getNumberOfPages() : 0)
                        .build())
                .toList();

        readingProgressRepo.saveAll(newRecords);
    }

    private void validateCurrentPage(int currentPage, int totalPages) {
        if (currentPage < 0) {
            throw new IllegalArgumentException("Current page cannot be negative");
        }
        if (totalPages > 0 && currentPage > totalPages) {
            throw new IllegalArgumentException(
                    String.format("Current page (%d) cannot exceed total pages (%d)", currentPage, totalPages)
            );
        }
    }


    private ReadingStatus determineStatus(int currentPage, int totalPages, ReadingStatus requestedStatus) {
        if (totalPages > 0 && currentPage == totalPages) {
            return ReadingStatus.COMPLETED;
        }
        if (currentPage > 0 && requestedStatus == ReadingStatus.WANT_TO_READ) {
            return ReadingStatus.READING;
        }
        return requestedStatus;
    }
}

