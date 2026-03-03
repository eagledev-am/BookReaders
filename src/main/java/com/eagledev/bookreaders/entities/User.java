package com.eagledev.bookreaders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.eagledev.bookreaders.entities.enums.Role;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "users")
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(updatable = false,unique = true)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(unique = true,nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String photoUrl;

    private LocalDate dateOfBirth;

    @CreationTimestamp
    private LocalDate joinedDate;

    @LastModifiedDate
    private LocalDate updateDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserContact> contacts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAuthor> followingAuthor;

    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        role.name()
                )
        );
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
