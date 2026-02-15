package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.room.PostResponse;
import com.eagledev.bookreaders.entities.Post;
import com.eagledev.bookreaders.entities.User;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    PostResponse postToPostResponse(Post post, UUID currentUserUUid);

    @Mapping(target = "authorUuid", source = "user.uuid")
    @Mapping(target = "authorName", source = "user.name")
    @Mapping(target = "authorPhotoUrl", source = "user.photoUrl")
//    @Mapping(target = "likedByCurrentUser" ,defaultValue = "false")
    @Mapping(target = "likeCount", defaultValue = "0")
    @Mapping(target = "commentCount", defaultValue = "0")
    @Mapping(target = "uuid", source = "postRequest.uuid")
    PostResponse postRequestToPost(Post postRequest, User user);

    @Mapping(target = "authorUuid", source = "post.user.uuid")
    @Mapping(target = "authorName", source = "post.user.name")
    @Mapping(target = "authorPhotoUrl", source = "post.user.photoUrl")
//    @Mapping(target = "likedByCurrentUser" ,defaultValue = "false" , source = "likedByCurrentUser")
    PostResponse postToPostResponse(Post post, boolean likedByCurrentUser);

}
