package com.eagledev.bookreaders.services.tracking.rating;

import com.eagledev.bookreaders.dtos.tracking.RatingRequest;
import com.eagledev.bookreaders.dtos.tracking.RatingResponse;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.UserRating;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.TrackingMapper;
import com.eagledev.bookreaders.repos.BookRepo;
import com.eagledev.bookreaders.repos.UserRatingRepo;
import com.eagledev.bookreaders.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookRatingServiceImpl implements BookRatingService {

    private final UserRatingRepo ratingRepo;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final TrackingMapper trackingMapper;

    @Override
    @Transactional
    public RatingResponse updateRating(UUID bookUuid, UUID userUuid, RatingRequest request) {
        Book book = bookRepo.findByUuid(bookUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "uuid", bookUuid));

        Optional<UserRating> existingRating = ratingRepo.findByUserUuidAndBookUuid(userUuid, bookUuid);

        UserRating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setScore(request.getScore());
        } else {
            User user = userRepo.findUserByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

            rating = UserRating.builder()
                    .user(user)
                    .book(book)
                    .score(request.getScore())
                    .build();
        }

        rating = ratingRepo.save(rating);
        return trackingMapper.toRatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getRating(UUID bookUuid, UUID userUuid) {
        if (!bookRepo.existsByUuid(bookUuid)) {
            throw new ResourceNotFoundException("Book", "uuid", bookUuid);
        }

        return ratingRepo.findByUserUuidAndBookUuid(userUuid, bookUuid)
                .map(trackingMapper::toRatingResponse)
                .orElse(null);
    }
}

