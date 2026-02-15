package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.room.CommentResponse;
import com.eagledev.bookreaders.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorUuid", source = "user.uuid")
    @Mapping(target = "authorName", source = "user.name")
    @Mapping(target = "authorPhotoUrl", source = "user.photoUrl")
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "replies")
    CommentResponse commentToCommentResponse(Comment comment);

    List<CommentResponse> commentsToCommentResponses(List<Comment> comments);

    @Mapping(target = "authorUuid", source = "comment.user.uuid")
    @Mapping(target = "authorName", source = "comment.user.name")
    @Mapping(target = "authorPhotoUrl", source = "comment.user.photoUrl")
    @Mapping(target = "replies", ignore = true)
    CommentResponse commentToCommentResponse(Comment comment, int replyCount);
}

