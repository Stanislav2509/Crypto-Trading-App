package com.cryptotrading.Crypto.Trading.App.repo;

import com.cryptotrading.Crypto.Trading.App.model.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByStripeSessionId(String stripeSessionId);
}
