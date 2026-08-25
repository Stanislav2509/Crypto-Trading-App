package com.cryptotrading.Crypto.Trading.App.repo;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);

    List<Transaction> findByCurrencyIsNull();

    Optional<Transaction> findByIdAndUser_Email(Long id, String email);
}
