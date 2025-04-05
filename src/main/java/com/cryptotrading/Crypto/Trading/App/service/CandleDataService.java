package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;

import java.util.List;

public interface CandleDataService {
    public List<CandleData> fetchCandles(String pair, int interval);
}
