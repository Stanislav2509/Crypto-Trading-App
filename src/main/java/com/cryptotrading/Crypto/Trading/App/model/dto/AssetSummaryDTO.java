package com.cryptotrading.Crypto.Trading.App.model.dto;

import java.math.BigDecimal;

public record AssetSummaryDTO(String symbol, BigDecimal totalQuantity, BigDecimal marketValueUsd, double profitLossUsd) {
}
