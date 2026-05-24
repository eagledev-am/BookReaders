package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "commerce_promocodes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Promocode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true, nullable = false)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int discountPercentage;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Promocode promocode = (Promocode) o;
        return Objects.equals(uuid, promocode.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

