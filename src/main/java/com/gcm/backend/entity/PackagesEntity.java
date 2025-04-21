package com.gcm.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "package")
@Data
public class PackagesEntity {
    private String packageName;
    private Integer contactTerm;
    private Double dailyProfit;
    private Double totalProfit;
    private String SettleInterestTime;
}
