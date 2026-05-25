package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Address;
import com.eagledev.bookreaders.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address, Long> {
    Optional<Address> findByUuid(UUID uuid);
    List<Address> findByUserUuid(UUID userUuid);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
}

