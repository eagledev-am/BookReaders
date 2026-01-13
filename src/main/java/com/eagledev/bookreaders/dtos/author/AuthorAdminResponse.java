package com.eagledev.bookreaders.dtos.author;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class AuthorAdminResponse{
    private UUID uuid;
    private String name;
    private String bio;
    private LocalDate dateOfBirth;
    private String nationality;
    private String photoUrl;
    private int followersCount;
    private int booksCount;
    List<AuthorBooksResponse> books;
    private boolean deleted;
    private LocalDateTime deletedAt;
}
