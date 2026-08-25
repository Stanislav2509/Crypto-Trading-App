package com.cryptotrading.Crypto.Trading.App.init;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.repo.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionCurrencyInit implements CommandLineRunner {
    private final TransactionRepository transactionRepository;

    public TransactionCurrencyInit(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        List<Transaction> transactionsMissingCurrency = transactionRepository.findByCurrencyIsNull();
        for (Transaction transaction : transactionsMissingCurrency) {
            transaction.setCurrency(MoneyCurrency.USD);
        }
        transactionRepository.saveAll(transactionsMissingCurrency);
    }
}
