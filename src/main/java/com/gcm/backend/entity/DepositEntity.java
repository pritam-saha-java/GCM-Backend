package com.gcm.backend.entity;

import com.gcm.backend.enums.DespositStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "deposit")
@Data
public class DepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "coin_type")
    private String coinType;

    @Column(name = "usd_amount")
    private Double usdAmount;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DespositStatusEnum status;

    @Column(name = "tx_id")
    private String txId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "screenshot_data", columnDefinition = "LONGBLOB")
    private byte[] screenshotData;
}
