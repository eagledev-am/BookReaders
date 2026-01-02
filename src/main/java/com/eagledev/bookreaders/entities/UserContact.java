package com.eagledev.bookreaders.entities;


import com.eagledev.bookreaders.entities.enums.ContactType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id" , "contact_type"})
        }
)
public class UserContact {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(nullable = false)
    private String contactValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType contactType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
