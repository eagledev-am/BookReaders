package com.eagledev.bookreaders.services.comment;

import com.eagledev.bookreaders.dtos.room.CommentRequest;
import com.eagledev.bookreaders.dtos.room.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    CommentResponse addComment(UUID postUuid, UUID currentUserUuid, CommentRequest request);

    CommentResponse replyToComment(UUID parentCommentUuid, UUID currentUserUuid, CommentRequest request);

    Page<CommentResponse> getCommentsByPost(UUID postUuid, Pageable pageable);

    CommentResponse updateComment(UUID commentUuid, UUID currentUserUuid, CommentRequest request);

    void deleteComment(UUID commentUuid, UUID currentUserUuid);


}

