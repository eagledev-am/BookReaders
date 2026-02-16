package com.eagledev.bookreaders.services.tracking;

import com.eagledev.bookreaders.dtos.tracking.ProgressRequest;
import com.eagledev.bookreaders.dtos.tracking.ProgressResponse;

import java.util.UUID;

public interface ReadingProgressService {

    ProgressResponse updateProgress(UUID bookUuid, UUID userUuid, ProgressRequest request);

    ProgressResponse getProgress(UUID bookUuid, UUID userUuid);
}

