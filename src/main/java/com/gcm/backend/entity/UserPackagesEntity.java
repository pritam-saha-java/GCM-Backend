package com.gcm.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_packages")
@Data
public class UserPackagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long packageId;
    private String userName;
    private Integer quantity;
    private LocalDateTime createdAt;
    private String txId;
    private LocalDateTime completionTime;

    @Column(name = "next_settle_time")
    private LocalDateTime nextSettleTime;

    @Column(name = "settled_count")
    private Integer settledCount = 0;
}
