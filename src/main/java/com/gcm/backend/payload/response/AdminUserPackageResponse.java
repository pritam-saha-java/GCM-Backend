package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class AdminUserPackageResponse {
    private Long id;
    private String userName;
    private String txId;
    private Integer quantity;

    private String createdAt;
    private String completionTime;

    private Long packageId;
    private String packageName;
    private Integer contactPrice;
    private Double dailyProfit;
    private Double totalProfit;

    private Double totalInvestment;
    private Double expectedTotalProfit;
}

