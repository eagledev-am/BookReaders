package com.eagledev.bookreaders.entities.enums;

public enum NotificationType {
    POST_LIKE,          // "User X liked your post" -> Redirect to Post
    POST_COMMENT,       // "User X commented on your post" -> Redirect to Post
    NEW_BOOK_RELEASE,   // "Author Y released a new book" -> Redirect to Book
    SYSTEM_ALERT        // "Maintenance scheduled for tonight" -> No redirect
}
