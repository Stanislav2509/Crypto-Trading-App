package com.cryptotrading.Crypto.Trading.App.model.dto;

import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;

import java.math.BigDecimal;
import java.util.List;

public record ChartDataDTO(
        String pair,
        String name,
        List<CandleData> candles,
        BigDecimal currentPrice,
        BigDecimal userBalance,
        BigDecimal quantityCrypto
) {
}
