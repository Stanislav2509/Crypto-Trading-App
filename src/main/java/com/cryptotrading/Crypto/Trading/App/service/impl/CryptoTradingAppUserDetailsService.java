package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CryptoTradingAppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CryptoTradingAppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).
                map(this::mapUserToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found!"));
    }

    private UserDetails mapUserToUserDetails(User user){
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
