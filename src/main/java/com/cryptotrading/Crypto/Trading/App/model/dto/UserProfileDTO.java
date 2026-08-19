package com.cryptotrading.Crypto.Trading.App.model.dto;

import java.math.BigDecimal;

public record UserProfileDTO(String firstName, String lastName, BigDecimal balance, String email) {
}
