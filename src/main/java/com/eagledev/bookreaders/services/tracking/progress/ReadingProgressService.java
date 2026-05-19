package com.eagledev.bookreaders.services.tracking.progress;

import com.eagledev.bookreaders.dtos.tracking.ProgressRequest;
import com.eagledev.bookreaders.dtos.tracking.ProgressResponse;

import java.util.List;
import java.util.UUID;

public interface ReadingProgressService {

    ProgressResponse updateProgress(UUID bookUuid, UUID userUuid, ProgressRequest request);
    ProgressResponse getProgress(UUID bookUuid, UUID userUuid);
    void grantDigitalAccess(UUID userUuid, List<UUID> bookUuids);
}

