package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "assets")
public class Asset extends BaseEntity{

    private double totalQuantity;
    private double moneyCurrency;
    private double priceDuringPurchase;
    private double priceNow;
    private double profitLoss;

    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;

    public double getMoneyCurrency() {
        return moneyCurrency;
    }

    public void setMoneyCurrency(double moneyCurrency) {
        this.moneyCurrency = moneyCurrency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CryptoType getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(CryptoType cryptoType) {
        this.cryptoType = cryptoType;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getPriceDuringPurchase() {
        return priceDuringPurchase;
    }

    public void setPriceDuringPurchase(double priceDuringPurchase) {
        this.priceDuringPurchase = priceDuringPurchase;
    }

    public double getPriceNow() {
        return priceNow;
    }

    public void setPriceNow(double priceNow) {
        this.priceNow = priceNow;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
}
