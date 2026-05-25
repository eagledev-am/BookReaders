package com.eagledev.bookreaders.services.post;

import com.eagledev.bookreaders.dtos.room.PostRequest;
import com.eagledev.bookreaders.dtos.room.PostResponse;
import com.eagledev.bookreaders.entities.DiscussionRoom;
import com.eagledev.bookreaders.entities.Post;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.PostMapper;
import com.eagledev.bookreaders.repos.PostRepo;
import com.eagledev.bookreaders.services.room.DiscussionRoomService;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepo repo;
    private final DiscussionRoomService roomService;
    private final UserService userService;
    private final PostMapper postMapper;


    @Override
    @Transactional
    public PostResponse createPost(UUID roomUUid, UUID currentUserUUid, PostRequest request) {
        DiscussionRoom room = roomService.getDiscussionRoomByUUid(roomUUid);
        User user = userService.getUserById(currentUserUUid);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .likeCount(0)
                .user(user)
                .room(room)
                .build();

        return postMapper.postRequestToPost(
                repo.save(post) ,  user
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(UUID roomUUid, UUID currentUserUUid, Pageable pageable) {
        if(!roomService.existsByUUid(roomUUid)){
            throw new ResourceNotFoundException("Discussion Room", "id",roomUUid);
        }

        Page<Post> postsPage = repo.findByRoomUuid(roomUUid,pageable);

        if (postsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> postIds = postsPage.map(Post::getUuid).toList();
        Set<UUID> likedPosts = repo.findPostUuidsLikedByUser(currentUserUUid, postIds);

        return postsPage.map(post -> postMapper.postToPostResponse(post, likedPosts.contains(post.getUuid())));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostDetails(UUID postUUid, UUID currentUserUUid) {
        Post post = getPostByUUid(postUUid);
        return postMapper.postToPostResponse(
                post , repo.isLikedByUser(post.getId(),currentUserUUid)
        );
    }

    @Override
    @Transactional
    public PostResponse updatePost(UUID postUUid, UUID currentUserUUid, PostRequest request) {
        Post post = getPostByUUid(postUUid);

        if(!isOwner(post,currentUserUUid)){
            throw new AuthorizationDeniedException("You are not the owner of this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post savedPost = repo.save(post);
        return postMapper.postToPostResponse(
                savedPost , repo.isLikedByUser(savedPost.getId(),currentUserUUid)
        );
    }

    @Override
    @Transactional
    public void deletePost(UUID postUuid, UUID currentUserUuid) {
        Post post = repo.findByUuid(postUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "uuid", postUuid));

        User user = userService.getUserById(currentUserUuid);

        if(!isOwner(post, currentUserUuid) && user.getRole() != Role.ADMIN) {
            throw new AuthorizationDeniedException("You are not the owner of this post");
        }
        repo.delete(post);
    }

    @Override
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return repo.findAll(pageable).map(post -> postMapper.postToPostResponse(post, false));
    }

    @Override
    public Page<PostResponse> searchPosts(String query, Pageable pageable) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isBlank()) {
            return getAllPosts(pageable);
        }
        return repo.searchPosts(normalized, pageable).map(post -> postMapper.postToPostResponse(post, false));
    }

    Post getPostByUUid(UUID postUUid){
        return repo.findByUuid(postUUid).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id",postUUid)
        );
    }

    private boolean isOwner(Post post, UUID currentUserUUid) {
        return post.getUser().getUuid().equals(currentUserUUid);
    }

}
