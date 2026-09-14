package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;


@Entity
@Table(name = "assets")
public class Asset extends BaseEntity{
    @Column(precision = 19, scale = 8)
    private BigDecimal totalQuantity;
    @Column(precision = 10, scale = 2)
    private BigDecimal moneyCurrency;
    @Column(precision = 19, scale = 8)
    private BigDecimal averagePurchasePrice;
    @Column(precision = 19, scale = 8)
    private BigDecimal priceNow;
    private double profitLoss;

    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getMoneyCurrency() {
        return moneyCurrency;
    }

    public void setMoneyCurrency(BigDecimal moneyCurrency) {
        this.moneyCurrency = moneyCurrency;
    }

    public BigDecimal getAveragePurchasePrice() {
        return averagePurchasePrice;
    }

    public void setAveragePurchasePrice(BigDecimal averagePurchasePrice) {
        this.averagePurchasePrice = averagePurchasePrice;
    }

    public BigDecimal getPriceNow() {
        return priceNow;
    }

    public void setPriceNow(BigDecimal priceNow) {
        this.priceNow = priceNow;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
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
}
