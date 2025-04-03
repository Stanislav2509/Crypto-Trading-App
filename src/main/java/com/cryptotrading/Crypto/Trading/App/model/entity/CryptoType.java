package com.cryptotrading.Crypto.Trading.App.model.entity;

import com.cryptotrading.Crypto.Trading.App.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "crypto_types")
public class CryptoType extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String symbol;
    @NotBlank
    private Double price;
    @OneToMany(mappedBy = "cryptoType")
    private List<Transaction> transactions;
    @OneToMany(mappedBy = "cryptoType")
    private List<Asset> assets;
}
