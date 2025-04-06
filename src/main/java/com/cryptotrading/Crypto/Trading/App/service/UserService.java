package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;

import java.util.Optional;

public interface UserService {
    boolean register(UserRegisterBindingModel userRegisterBindingModel);
    Optional<Transaction> buyCrypto(String username, String pair, double spend, double quantity);

    double getBalance(String email);

    double getQuantityFromPair(String email, String pair);

    Optional<Transaction> sellCrypto(String email, String pair, double spend, double quantity);
    User findByEmail(String email);

    void resetProfile(String email);
}
