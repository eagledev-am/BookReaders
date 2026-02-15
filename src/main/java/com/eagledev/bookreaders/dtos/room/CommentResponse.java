package com.eagledev.bookreaders.dtos.room;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CommentResponse {
    private UUID uuid;
    private String text;

    // Author Details
    private UUID authorUuid;
    private String authorName;
    private String authorPhotoUrl;

    private LocalDate createdAt;

    private int replyCount;
    private List<CommentResponse> replies;
}
