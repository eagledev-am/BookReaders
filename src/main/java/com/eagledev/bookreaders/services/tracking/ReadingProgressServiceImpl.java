package com.eagledev.bookreaders.services.tracking;

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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final UserReadingProgressRepo progressRepo;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final TrackingMapper trackingMapper;

    @Override
    @Transactional
    public ProgressResponse updateProgress(UUID bookUuid, UUID userUuid, ProgressRequest request) {
        Book book = bookRepo.findByUuid(bookUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "uuid", bookUuid));

        int totalPages = book.getNumberOfPages() != null ? book.getNumberOfPages() : 0;
        int currentPage = request.getCurrentPage();

        validateCurrentPage(currentPage, totalPages);

        Optional<UserReadingProgress> existingProgress = progressRepo.findByUserUuidAndBookUuid(userUuid, bookUuid);

        UserReadingProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCurrentPage(currentPage);
            progress.setStatus(determineStatus(currentPage, totalPages, request.getStatus()));
            progress.setLastReadAt(LocalDateTime.now());
        } else {
            User user = userRepo.findUserByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

            progress = UserReadingProgress.builder()
                    .user(user)
                    .book(book)
                    .currentPage(currentPage)
                    .totalPages(totalPages)
                    .status(determineStatus(currentPage, totalPages, request.getStatus()))
                    .lastReadAt(LocalDateTime.now())
                    .build();
        }

        progress = progressRepo.save(progress);
        return trackingMapper.toProgressResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressResponse getProgress(UUID bookUuid, UUID userUuid) {
        if (!bookRepo.existsByUuid(bookUuid)) {
            throw new ResourceNotFoundException("Book", "uuid", bookUuid);
        }

        return progressRepo.findByUserUuidAndBookUuid(userUuid, bookUuid)
                .map(trackingMapper::toProgressResponse)
                .orElse(null);
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

