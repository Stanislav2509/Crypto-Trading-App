package com.cryptotrading.Crypto.Trading.App.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);
}
