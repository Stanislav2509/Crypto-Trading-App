package com.cryptotrading.Crypto.Trading.App.model.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class TradeBindingModel {

    private String pair;
    @Column(precision = 19, scale = 8)
    private BigDecimal spend;

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public BigDecimal getSpend() {
        return spend;
    }

    public void setSpend(BigDecimal spend) {
        this.spend = spend;
    }
}
