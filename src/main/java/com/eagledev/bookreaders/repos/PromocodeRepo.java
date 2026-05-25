package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Promocode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PromocodeRepo extends JpaRepository<Promocode, Long> {
    Optional<Promocode> findByUuid(UUID uuid);
    Optional<Promocode> findByCodeIgnoreCaseAndIsActiveTrue(String code);
    boolean existsByCodeIgnoreCase(String code);


    Page<Promocode> findAll(Pageable pageable);
    Page<Promocode> findByCodeContainingIgnoreCase(String code , Pageable pageable);
}
