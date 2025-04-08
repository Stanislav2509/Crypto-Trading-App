package com.cryptotrading.Crypto.Trading.App.model.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class AssetDTO {
    @Column(precision = 10, scale = 2)
    private BigDecimal moneyCurrency;
    @Column(precision = 10, scale = 2)
    private double profitLoss;

    public BigDecimal getMoneyCurrency() {
        return moneyCurrency;
    }

    public void setMoneyCurrency(BigDecimal moneyCurrency) {
        this.moneyCurrency = moneyCurrency;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
}
