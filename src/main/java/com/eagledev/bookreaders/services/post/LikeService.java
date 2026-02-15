package com.eagledev.bookreaders.services.post;

import java.util.UUID;

public interface LikeService {
    /**
     * Toggles the like status on a post.
     * @param postUuid The UUID of the post to like/unlike
     * @param currentUserUuid The UUID of the current user
     * @return true if the post is now liked, false if unliked
     */
    boolean toggleLike(UUID postUuid, UUID currentUserUuid);
}
