package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderUuid(UUID orderUuid);
}

