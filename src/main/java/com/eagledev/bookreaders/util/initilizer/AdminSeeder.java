package com.eagledev.bookreaders.util.initilizer;

import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.enums.Role;
import com.eagledev.bookreaders.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.existsByEmail("admin@eagledev.com")) {
            log.info("--> Admin user already exists.");
            return;
        }

        User admin = User.builder()
                .name("Super")
                .email("admin@eagledev.com")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);

        log.info("--> [AdminUserSeeder] Admin user created: admin@eagledev.com / admin");
    }
}
