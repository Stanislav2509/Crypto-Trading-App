package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;

public interface TransactionService {
    Transaction  findById(Long id);
}
