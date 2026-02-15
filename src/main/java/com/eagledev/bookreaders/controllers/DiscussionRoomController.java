package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.dtos.api.ApiResponse;
import com.eagledev.bookreaders.dtos.api.ApiResponseBuilder;
import com.eagledev.bookreaders.dtos.room.DiscussionRoomResponse;
import com.eagledev.bookreaders.services.room.DiscussionRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Social - Discussion Rooms", description = "Get the room for a specific book")
public class DiscussionRoomController {

    private final DiscussionRoomService roomService;

    @Operation(summary = "Get Discussion Room for a Book",
            description = "Returns the discussion room details for a specific book")
    @GetMapping("/books/{bookUuid}/discussion-room")
    public ResponseEntity<ApiResponse<DiscussionRoomResponse>> getRoomForBook(@PathVariable UUID bookUuid) {
        DiscussionRoomResponse room = roomService.getDiscussionRoom(bookUuid);
        return ResponseEntity.ok(
                ApiResponseBuilder.success("Discussion room retrieved successfully", room)
        );
    }
}