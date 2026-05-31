package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;

import java.util.Map;

public interface CryptoTypeService {
    Map<String, String> getPairsPrices();

    CryptoType findBySymbol(String pair);
}
