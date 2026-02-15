package com.eagledev.bookreaders.services.room;

import com.eagledev.bookreaders.dtos.room.DiscussionRoomResponse;
import com.eagledev.bookreaders.entities.DiscussionRoom;

import java.util.UUID;

public interface DiscussionRoomService {
    DiscussionRoomResponse getDiscussionRoom(UUID bookId);
    DiscussionRoom getDiscussionRoomByUUid(UUID roomId);
    boolean existsByUUid(UUID roomId);
}
