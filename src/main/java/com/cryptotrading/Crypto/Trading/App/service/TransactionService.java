package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction  findById(Long id);

    List<Transaction> getHistoryByEmail(String name);
}
