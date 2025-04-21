package com.gcm.backend.entity;

import com.gcm.backend.enums.DespositStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw")
@Data
public class WithdrawEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount_in_usd")
    private Double amountInUsd;

    @Column(name = "coin_type")
    private Long selectedCurrency;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DespositStatusEnum status;

    @Column(name = "tx_id")
    private String txId;

    @Column(name = "receivable")
    private Double receivable;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
