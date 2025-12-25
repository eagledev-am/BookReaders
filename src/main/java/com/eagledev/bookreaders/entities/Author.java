package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Author {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String bio;

    private LocalDate dateOfBirth;

    private String photoUrl;

    private String nationality;

    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "authors")
    private List<Book> books;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "author")
    private List<UserAuthor> followers;
}
