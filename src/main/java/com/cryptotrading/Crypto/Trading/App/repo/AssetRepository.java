package com.cryptotrading.Crypto.Trading.App.repo;

import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCryptoTypeAndUser(CryptoType cryptoType, User user);

    List<Asset> findAllByUser(User user);

    List<Asset> findAllByCryptoType(CryptoType cryptoType);

    Optional<Asset> findByCryptoTypeAndUserId(CryptoType cryptoType, Long currentUserId);
}
