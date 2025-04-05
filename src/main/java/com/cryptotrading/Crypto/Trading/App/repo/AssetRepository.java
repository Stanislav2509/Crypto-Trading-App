package com.cryptotrading.Crypto.Trading.App.repo;

import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
}
