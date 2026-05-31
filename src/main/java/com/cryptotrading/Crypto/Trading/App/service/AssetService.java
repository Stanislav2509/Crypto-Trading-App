package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;

import java.util.List;

public interface AssetService {
    List<Asset> findAllByUserEmail(String email);
}
