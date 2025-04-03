package com.cryptotrading.Crypto.Trading.App.model.entity;

import com.cryptotrading.Crypto.Trading.App.model.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @ManyToOne
    private User user;
    @ManyToOne
    private CryptoType cryptoType;
    @NotBlank
    private Double quantity;
    @NotBlank
    private LocalDateTime dateTime;
    @NotBlank
    private String transactionType;
}
