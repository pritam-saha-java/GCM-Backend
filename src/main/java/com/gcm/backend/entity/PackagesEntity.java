package com.gcm.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "package")
@Data
public class PackagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "contact_price")
    private Integer contactPrice;

    @Column(name = "contact_term")
    private Integer contactTerm;

    @Column(name = "daily_profit")
    private Double dailyProfit;

    @Column(name = "total_profit")
    private Double totalProfit;

    @Column(name = "Settle_interest_time")
    private String SettleInterestTime;

    @Column(name = "level_1_bonus")
    private Double level1Bonus;

    @Column(name = "level_2_bonus")
    private Double level2Bonus;

    @Column(name = "level_3_bonus")
    private Double level3Bonus;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
}
