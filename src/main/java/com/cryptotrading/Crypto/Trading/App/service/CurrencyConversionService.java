package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;

import java.math.BigDecimal;

public interface CurrencyConversionService {
    BigDecimal convert(BigDecimal amount, MoneyCurrency from, MoneyCurrency to);
}
