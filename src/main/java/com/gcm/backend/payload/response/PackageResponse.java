package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class PackageResponse {
    private Integer contactPrice;
    private Long id;
    private String packageName;
    private Integer contactTerm;
    private Double dailyProfit;
    private Double totalProfit;
    private String settleInterestTime;
    private Double level1Bonus;
    private Double level2Bonus;
    private Double level3Bonus;
    private String imageBase64;
}

