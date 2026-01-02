package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Author {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(updatable = false,unique = true)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

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
