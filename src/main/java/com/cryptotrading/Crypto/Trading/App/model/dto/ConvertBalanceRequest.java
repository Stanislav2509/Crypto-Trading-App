package com.cryptotrading.Crypto.Trading.App.model.dto;

import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;

public record ConvertBalanceRequest(MoneyCurrency targetCurrency) {
}
