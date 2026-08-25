package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.TransactionDTO;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.service.CurrencyConversionService;
import com.cryptotrading.Crypto.Trading.App.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final CurrencyConversionService currencyConversionService;

    public TransactionRestController(TransactionService transactionService, CurrencyConversionService currencyConversionService) {
        this.transactionService = transactionService;
        this.currencyConversionService = currencyConversionService;
    }

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactionHistory(Principal principal) {
        return transactionService.getHistoryByEmail(principal.getName())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id, Principal principal) {
        return transactionService.findByIdForUser(id, principal.getName())
                .map(transaction -> ResponseEntity.ok(toDTO(transaction)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private TransactionDTO toDTO(Transaction transaction) {
        double displayProfitLoss = transaction.getProfitLoss();
        if (transaction.getCurrency() == MoneyCurrency.EUR) {
            displayProfitLoss = currencyConversionService
                    .convert(BigDecimal.valueOf(transaction.getProfitLoss()), MoneyCurrency.USD, MoneyCurrency.EUR)
                    .doubleValue();
        }

        return new TransactionDTO(
                transaction.getCryptoType().getSymbol(),
                transaction.getTransactionType(),
                transaction.getSpendMoney(),
                transaction.getReceiveCrypto(),
                transaction.getSpendCrypto(),
                transaction.getReceiveMoney(),
                transaction.getCurrCryptoPrice(),
                transaction.getCurrency(),
                transaction.getDateTime(),
                displayProfitLoss
        );
    }
}
