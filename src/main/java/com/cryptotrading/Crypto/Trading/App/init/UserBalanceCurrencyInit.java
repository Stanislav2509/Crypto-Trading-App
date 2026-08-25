package com.cryptotrading.Crypto.Trading.App.init;

import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserBalanceCurrencyInit implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserBalanceCurrencyInit(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        List<User> usersMissingCurrency = userRepository.findByBalanceCurrencyIsNull();
        for (User user : usersMissingCurrency) {
            user.setBalanceCurrency(MoneyCurrency.USD);
        }
        userRepository.saveAll(usersMissingCurrency);
    }
}
