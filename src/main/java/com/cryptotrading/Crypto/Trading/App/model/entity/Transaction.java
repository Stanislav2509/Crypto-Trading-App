package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;
    @Column(precision = 19, scale = 8)
    private BigDecimal receiveCrypto;
    @Column(precision = 19, scale = 8)
    private BigDecimal spendCrypto;
    @Column(precision = 10, scale = 2)
    private  BigDecimal receiveMoney;
    @Column(precision = 10, scale = 2)
    private BigDecimal spendMoney;
    @Column(precision = 19, scale = 8)
    private BigDecimal currCryptoPrice;
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

    public BigDecimal getReceiveCrypto() {
        return receiveCrypto;
    }

    public void setReceiveCrypto(BigDecimal receiveCrypto) {
        this.receiveCrypto = receiveCrypto;
    }

    public BigDecimal getSpendCrypto() {
        return spendCrypto;
    }

    public void setSpendCrypto(BigDecimal spendCrypto) {
        this.spendCrypto = spendCrypto;
    }

    public BigDecimal getReceiveMoney() {
        return receiveMoney;
    }

    public void setReceiveMoney(BigDecimal receiveMoney) {
        this.receiveMoney = receiveMoney;
    }

    public BigDecimal getSpendMoney() {
        return spendMoney;
    }

    public void setSpendMoney(BigDecimal spendMoney) {
        this.spendMoney = spendMoney;
    }

    public BigDecimal getCurrCryptoPrice() {
        return currCryptoPrice;
    }

    public void setCurrCryptoPrice(BigDecimal currCryptoPrice) {
        this.currCryptoPrice = currCryptoPrice;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
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
}
