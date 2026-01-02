package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.UserContact;
import com.eagledev.bookreaders.entities.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserContactRepo extends JpaRepository<UserContact,Integer> {
    Optional<UserContact> findUserContactByUserAndContactType(User user, ContactType type);
}
