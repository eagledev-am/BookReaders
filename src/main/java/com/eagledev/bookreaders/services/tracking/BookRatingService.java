package com.eagledev.bookreaders.services.tracking;

import com.eagledev.bookreaders.dtos.tracking.RatingRequest;
import com.eagledev.bookreaders.dtos.tracking.RatingResponse;

import java.util.UUID;

public interface BookRatingService {

    RatingResponse updateRating(UUID bookUuid, UUID userUuid, RatingRequest request);

    RatingResponse getRating(UUID bookUuid, UUID userUuid);
}

