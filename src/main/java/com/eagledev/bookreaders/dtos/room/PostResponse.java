package com.eagledev.bookreaders.dtos.room;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PostResponse {
    private UUID uuid;
    private String title;
    private String content;
    private UUID authorUuid;
    private String authorName;
    private String authorPhotoUrl;
    private int likeCount;
    private int commentCount;
    private LocalDate createdAt;
    private boolean likedByCurrentUser;
}
