package com.eagledev.bookreaders.entities;

import com.eagledev.bookreaders.entities.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_reading_book_progress" , uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id" , "book_id"
                        }
                )
}
)
public class UserReadingProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReadingStatus status;
    private int currentPage; // e.g. Page 54
    private int totalPages;  // Copied from Book entity for percentage calc

    @UpdateTimestamp
    private LocalDateTime lastReadAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
}
