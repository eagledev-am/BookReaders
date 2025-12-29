package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.VerificationCode;
import com.eagledev.bookreaders.entities.enums.CodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode,Integer> {
    Optional<VerificationCode> findByUserAndCodeAndType(User user, String code, CodeType type);

    void deleteByUserAndType(User user, CodeType type);
}
