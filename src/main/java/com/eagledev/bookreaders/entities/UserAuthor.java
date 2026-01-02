package com.eagledev.bookreaders.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(
    name = "user_follower_author" , uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                    "user_id" , "author_id"
            })
}
)
public class UserAuthor {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @CreationTimestamp
    private LocalDate followedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
}
