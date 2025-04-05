package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;

public interface UserService {
    boolean register(UserRegisterBindingModel userRegisterBindingModel);
}
