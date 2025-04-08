package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserService {
    boolean register(UserRegisterBindingModel userRegisterBindingModel);
    Optional<Transaction> buyCrypto(String username, String pair, BigDecimal spend, BigDecimal quantity);

    BigDecimal getBalance(String email);

    BigDecimal getQuantityFromPair(String email, String pair);

    Optional<Transaction> sellCrypto(String email, String pair, BigDecimal spend, BigDecimal quantity);
    User findByEmail(String email);

    void resetProfile(String email);
}
