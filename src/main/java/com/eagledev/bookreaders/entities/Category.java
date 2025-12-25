package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private String description;

    @Column(unique = true,nullable = false)
    private String tag;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "categories")
    private List<Book> books;
}
