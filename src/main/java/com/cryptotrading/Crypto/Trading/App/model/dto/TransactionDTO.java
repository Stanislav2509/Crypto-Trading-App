package com.cryptotrading.Crypto.Trading.App.model.dto;

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
        LocalDateTime dateTime,
        double profitLoss
) {
}
