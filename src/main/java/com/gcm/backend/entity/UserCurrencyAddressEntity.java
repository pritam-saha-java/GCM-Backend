package com.gcm.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "User_currency_address")
@Data
public class UserCurrencyAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "coins_id")
    private Long coinsId;

    @Column(name = "coin_name")
    private String coinName;

    @Column(name = "wallet_address")
    private String walletAddress;

}
