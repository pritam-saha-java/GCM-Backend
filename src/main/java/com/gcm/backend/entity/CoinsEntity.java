package com.gcm.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coins")
@Data
public class CoinsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "coin_name")
    private String coinName;

    @Column(name = "conversion_rate")
    private Double conversionRate;

    @Column(name = "admin_wallet_address")
    private String adminWalletAddress;
}
