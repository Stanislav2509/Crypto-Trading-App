package com.cryptotrading.Crypto.Trading.App.model.enums;

public enum TransactionType {
    BUY("Buy"),
    SELL("Sell");

    private final String displayValue;

    TransactionType(String displayValue) {this.displayValue = displayValue;}
    public String getDisplayValue(){
        return this.displayValue;
    }
}
