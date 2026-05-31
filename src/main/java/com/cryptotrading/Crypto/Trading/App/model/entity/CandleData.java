package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Column;

import java.math.BigDecimal;


public class CandleData {
    @Column(precision = 4, scale = 2)
    private BigDecimal time;
    @Column(precision = 19, scale = 8)
    private BigDecimal open;
    @Column(precision = 19, scale = 8)
    private BigDecimal high;
    @Column(precision = 19, scale = 8)
    private BigDecimal low;
    @Column(precision = 19, scale = 8)
    private BigDecimal close;

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }
}
