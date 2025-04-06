package com.cryptotrading.Crypto.Trading.App.repo;

import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoTypeRepository extends JpaRepository<CryptoType, Long> {
    Optional<CryptoType> findBySymbol(String pair);
}
