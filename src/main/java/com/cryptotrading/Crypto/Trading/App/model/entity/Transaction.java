package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;
    private double receiveCrypto;
    private double spendCrypto;
    private  double receiveMoney;
    private double spendMoney;
    private double currCryptoPrice;
    private double profitLoss;
    private LocalDateTime dateTime;
    @NotBlank
    private String transactionType;
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

    public double getReceiveCrypto() {
        return receiveCrypto;
    }

    public void setReceiveCrypto(double receiveCrypto) {
        this.receiveCrypto = receiveCrypto;
    }

    public double getSpendCrypto() {
        return spendCrypto;
    }

    public void setSpendCrypto(double spendCrypto) {
        this.spendCrypto = spendCrypto;
    }

    public double getReceiveMoney() {
        return receiveMoney;
    }

    public void setReceiveMoney(double receiveMoney) {
        this.receiveMoney = receiveMoney;
    }

    public double getSpendMoney() {
        return spendMoney;
    }

    public void setSpendMoney(double spendMoney) {
        this.spendMoney = spendMoney;
    }

    public double getCurrCryptoPrice() {
        return currCryptoPrice;
    }

    public void setCurrCryptoPrice(double currCryptoPrice) {
        this.currCryptoPrice = currCryptoPrice;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
}
