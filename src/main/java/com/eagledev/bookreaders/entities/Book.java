package com.eagledev.bookreaders.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
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
public class Book{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(updatable = false,unique = true)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String edition;

    private Integer numberOfPages;

    private BigDecimal price;

    private String language;

    private LocalDate publishDate;

    private String ebookCoverUrl;

    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @ManyToMany
    @JoinTable(
            name = "authors_books" ,
            joinColumns = @JoinColumn(name = "book_id") ,
            inverseJoinColumns = @JoinColumn(name = "author_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"})
    )
    private List<Author> authors;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    private List<UserRating> ratings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "category_id"})
    )
    private List<Category> categories;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private DiscussionRoom discussionRoom;

    public void setAuthors(List<Author> authors) {
        this.authors.clear();
        this.authors.addAll(authors);
    }

    public void setCategories(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }
}
