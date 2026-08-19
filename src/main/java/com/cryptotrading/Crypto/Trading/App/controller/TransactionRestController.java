package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.TransactionDTO;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionRestController {

    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactionHistory(Principal principal) {
        return transactionService.getHistoryByEmail(principal.getName())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/transactions/{id}")
    public TransactionDTO getTransaction(@PathVariable Long id) {
        return toDTO(transactionService.findById(id));
    }

    private TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getCryptoType().getSymbol(),
                transaction.getTransactionType(),
                transaction.getSpendMoney(),
                transaction.getReceiveCrypto(),
                transaction.getSpendCrypto(),
                transaction.getReceiveMoney(),
                transaction.getCurrCryptoPrice(),
                transaction.getDateTime(),
                transaction.getProfitLoss()
        );
    }
}
