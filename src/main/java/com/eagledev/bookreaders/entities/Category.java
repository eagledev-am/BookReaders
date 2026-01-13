package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private String description;

    @Column(unique = true,nullable = false)
    private String tag;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "categories")
    private List<Book> books;
}
