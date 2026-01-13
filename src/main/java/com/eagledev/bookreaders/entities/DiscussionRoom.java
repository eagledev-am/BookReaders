package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table
public class DiscussionRoom {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(updatable = false,unique = true)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

    @CreationTimestamp
    private String createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    List<Post> posts;

}
