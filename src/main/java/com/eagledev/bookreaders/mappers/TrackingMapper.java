package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.tracking.ProgressResponse;
import com.eagledev.bookreaders.dtos.tracking.RatingResponse;
import com.eagledev.bookreaders.entities.UserRating;
import com.eagledev.bookreaders.entities.UserReadingProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TrackingMapper {
    @Mapping(target = "percentage", source = ".", qualifiedByName = "calculatePercentage")
    ProgressResponse toProgressResponse(UserReadingProgress entity);

    RatingResponse toRatingResponse(UserRating entity);

    @Named("calculatePercentage")
    default double calculatePercentage(UserReadingProgress progress) {
        if (progress == null || progress.getTotalPages() <= 0) {
            return 0.0;
        }

        double percentage = ((double) progress.getCurrentPage() / progress.getTotalPages()) * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }
}

