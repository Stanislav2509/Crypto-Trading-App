package com.cryptotrading.Crypto.Trading.App.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity{
    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;
    @NotBlank
    private double total_quantity;
}
