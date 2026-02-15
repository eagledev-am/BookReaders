package com.eagledev.bookreaders.dtos.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class DiscussionRoomResponse {
    private UUID uuid;
    private UUID bookUuid;
    private String bookTitle;
    private int totalPosts;
}
