package com.cryptotrading.Crypto.Trading.App.model.dto;

public class AssetDTO {
    private double moneyCurrency;
    private double profitLoss;

    public double getMoneyCurrency() {
        return moneyCurrency;
    }

    public void setMoneyCurrency(double moneyCurrency) {
        this.moneyCurrency = moneyCurrency;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
}
