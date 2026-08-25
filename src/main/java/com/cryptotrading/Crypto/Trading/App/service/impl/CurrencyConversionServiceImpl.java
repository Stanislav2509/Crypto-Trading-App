package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.service.CurrencyConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    static final BigDecimal USD_TO_EUR_RATE = new BigDecimal("0.86");

    @Override
    public BigDecimal convert(BigDecimal amount, MoneyCurrency from, MoneyCurrency to) {
        if (from == MoneyCurrency.USD && to == MoneyCurrency.EUR) {
            return amount.multiply(USD_TO_EUR_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        if (from == MoneyCurrency.EUR && to == MoneyCurrency.USD) {
            return amount.divide(USD_TO_EUR_RATE, 2, RoundingMode.HALF_UP);
        }
        throw new IllegalArgumentException("Unsupported currency conversion: " + from + " -> " + to);
    }
}
