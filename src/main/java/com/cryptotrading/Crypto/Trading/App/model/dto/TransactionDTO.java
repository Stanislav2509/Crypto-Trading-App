package com.cryptotrading.Crypto.Trading.App.model.dto;

import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        String cryptoSymbol,
        String transactionType,
        BigDecimal spendMoney,
        BigDecimal receiveCrypto,
        BigDecimal spendCrypto,
        BigDecimal receiveMoney,
        BigDecimal currCryptoPrice,
        MoneyCurrency currency,
        LocalDateTime dateTime,
        double profitLoss
) {
}
