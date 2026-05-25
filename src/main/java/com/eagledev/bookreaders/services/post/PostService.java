package com.eagledev.bookreaders.services.post;

import com.eagledev.bookreaders.dtos.room.PostRequest;
import com.eagledev.bookreaders.dtos.room.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    PostResponse createPost(UUID roomUUid , UUID currentUserUUid,  PostRequest request);
    Page<PostResponse> getPosts(UUID roomUUid , UUID currentUserUUid,Pageable pageable);
    PostResponse getPostDetails(UUID postUUid , UUID currentUserUUid);
    PostResponse updatePost(UUID postUUid ,UUID currentUserUUid ,PostRequest request);
    void deletePost(UUID postUuid, UUID currentUserUUid);
    Page<PostResponse> getAllPosts(Pageable pageable);
    Page<PostResponse> searchPosts(String query, Pageable pageable);
}
