package com.eagledev.bookreaders.services.room;

import com.eagledev.bookreaders.dtos.room.DiscussionRoomResponse;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.DiscussionRoom;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.DiscussionRoomRepo;
import com.eagledev.bookreaders.services.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscussionRoomServiceImp implements DiscussionRoomService{

    private final DiscussionRoomRepo roomRepo;
    private final BookService bookService;

    @Override
    public DiscussionRoomResponse getDiscussionRoom(UUID bookId) {
        if(!bookService.isExistedByUuid(bookId)){
            throw new ResourceNotFoundException("Book", "id",bookId);
        }
        DiscussionRoom room = roomRepo.findDiscussionRoomByBookUuid(bookId);
        int totalPosts = roomRepo.countPostsByRoomId(room.getId());
        return toResponse(room, totalPosts);
    }

    @Override
    public DiscussionRoom getDiscussionRoomByUUid(UUID roomId) {
        return roomRepo.findByUuid(roomId).orElseThrow(
                () -> new ResourceNotFoundException("Discussion Room", "id",roomId)
        );
    }

    @Override
    public boolean existsByUUid(UUID roomId) {
        return roomRepo.existsByUuid(roomId);
    }

    DiscussionRoomResponse toResponse(DiscussionRoom room , int totalPosts){
        Book book = room.getBook();

        return DiscussionRoomResponse.builder()
                .uuid(room.getUuid())
                .bookUuid(book.getUuid())
                .bookTitle(book.getTitle())
                .totalPosts(totalPosts)
                .build();
    }
}
