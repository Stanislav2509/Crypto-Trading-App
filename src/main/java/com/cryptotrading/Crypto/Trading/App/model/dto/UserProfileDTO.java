package com.cryptotrading.Crypto.Trading.App.model.dto;

import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;

import java.math.BigDecimal;

public record UserProfileDTO(String firstName, String lastName, BigDecimal balance, MoneyCurrency balanceCurrency, String email) {
}
