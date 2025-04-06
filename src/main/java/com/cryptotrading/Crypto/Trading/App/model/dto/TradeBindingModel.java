package com.cryptotrading.Crypto.Trading.App.model.dto;

public class TradeBindingModel {

    private String pair;

    private double spend;
    private double receive;

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public double getSpend() {
        return spend;
    }

    public void setSpend(double spend) {
        this.spend = spend;
    }

    public double getReceive() {
        return receive;
    }

    public void setReceive(double receive) {
        this.receive = receive;
    }
}
