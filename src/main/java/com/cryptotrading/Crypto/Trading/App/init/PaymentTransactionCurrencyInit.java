package com.cryptotrading.Crypto.Trading.App.init;

import com.cryptotrading.Crypto.Trading.App.model.entity.PaymentTransaction;
import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.repo.PaymentTransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentTransactionCurrencyInit implements CommandLineRunner {
    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionCurrencyInit(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public void run(String... args) {
        List<PaymentTransaction> transactionsMissingCurrency = paymentTransactionRepository.findByCurrencyIsNull();
        for (PaymentTransaction transaction : transactionsMissingCurrency) {
            transaction.setCurrency(MoneyCurrency.USD);
        }
        paymentTransactionRepository.saveAll(transactionsMissingCurrency);
    }
}
