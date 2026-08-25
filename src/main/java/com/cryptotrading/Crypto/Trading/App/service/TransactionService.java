package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Optional<Transaction> findByIdForUser(Long id, String email);

    List<Transaction> getHistoryByEmail(String name);
}
