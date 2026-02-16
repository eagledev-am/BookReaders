package com.eagledev.bookreaders.dtos.tracking;

import com.eagledev.bookreaders.entities.enums.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {
    private int currentPage;
    private int totalPages;
    private double percentage;
    private ReadingStatus status;
    private LocalDateTime lastReadAt;
}

