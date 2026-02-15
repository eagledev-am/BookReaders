package com.eagledev.bookreaders.services.comment;

import com.eagledev.bookreaders.dtos.room.CommentRequest;
import com.eagledev.bookreaders.dtos.room.CommentResponse;
import com.eagledev.bookreaders.entities.Comment;
import com.eagledev.bookreaders.entities.Post;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.CommentMapper;
import com.eagledev.bookreaders.repos.CommentRepo;
import com.eagledev.bookreaders.repos.PostRepo;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponse addComment(UUID postUuid, UUID currentUserUuid, CommentRequest request) {
        Post post = postRepo.findByUuid(postUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "uuid", postUuid));

        User user = userService.getUserById(currentUserUuid);

        Comment comment = Comment.builder()
                .text(request.getText())
                .post(post)
                .user(user)
                .build();

        Comment savedComment = commentRepo.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepo.save(post);

        CommentResponse response = commentMapper.commentToCommentResponse(savedComment);
        response.setReplyCount(0);
        return response;
    }

    @Override
    @Transactional
    public CommentResponse replyToComment(UUID parentCommentUuid, UUID currentUserUuid, CommentRequest request) {
        Comment targetComment = commentRepo.findByUuid(parentCommentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "uuid", parentCommentUuid));

        User user = userService.getUserById(currentUserUuid);

        Comment actualParent;
        if (targetComment.getParent() != null) {
            actualParent = targetComment.getParent();
        } else {
            actualParent = targetComment;
        }

        Comment reply = Comment.builder()
                .text(request.getText())
                .post(actualParent.getPost())
                .user(user)
                .parent(actualParent)
                .build();

        Comment savedReply = commentRepo.save(reply);

        Post post = actualParent.getPost();
        post.setCommentCount(post.getCommentCount() + 1);
        postRepo.save(post);

        CommentResponse response = commentMapper.commentToCommentResponse(savedReply);
        response.setReplyCount(0);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPost(UUID postUuid, Pageable pageable) {
        if (postRepo.findByUuid(postUuid).isEmpty()) {
            throw new ResourceNotFoundException("Post", "uuid", postUuid);
        }

        Page<Comment> commentsPage = commentRepo.findTopLevelCommentsByPostUuid(postUuid, pageable);

        return commentsPage.map(comment -> {
            CommentResponse response = commentMapper.commentToCommentResponse(comment);
            response.setReplyCount(commentRepo.countRepliesByParentUuid(comment.getUuid()));
            return response;
        });
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentUuid, UUID currentUserUuid, CommentRequest request) {
        Comment comment = commentRepo.findByUuid(commentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "uuid", commentUuid));

        if (!comment.getUser().getUuid().equals(currentUserUuid)) {
            throw new AccessDeniedException("You are not the owner of this comment");
        }

        comment.setText(request.getText());

        Comment savedComment = commentRepo.save(comment);

        CommentResponse response = commentMapper.commentToCommentResponse(savedComment);
        response.setReplyCount(commentRepo.countRepliesByParentUuid(commentUuid));
        return response;
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentUuid, UUID currentUserUuid) {
        Comment comment = commentRepo.findByUuid(commentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "uuid", commentUuid));

        if (!comment.getUser().getUuid().equals(currentUserUuid)) {
            throw new AccessDeniedException("You are not the owner of this comment");
        }

        Post post = comment.getPost();

        int repliesCount = commentRepo.countRepliesByParentUuid(commentUuid);
        int totalToRemove = 1 + repliesCount;

        commentRepo.delete(comment);

        post.setCommentCount(Math.max(0, post.getCommentCount() - totalToRemove));
        postRepo.save(post);
    }
}
