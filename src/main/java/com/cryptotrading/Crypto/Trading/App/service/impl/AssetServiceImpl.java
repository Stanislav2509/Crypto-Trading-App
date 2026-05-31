package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.repo.AssetRepository;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.cryptotrading.Crypto.Trading.App.service.AssetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public AssetServiceImpl(AssetRepository assetRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Asset> findAllByUserEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user = new User();
        if(userOpt.isPresent()){
            user= userOpt.get();
        }

        return assetRepository.findAllByUser(user);
    }
}
