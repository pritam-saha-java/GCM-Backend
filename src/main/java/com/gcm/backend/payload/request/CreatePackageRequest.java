package com.gcm.backend.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePackageRequest {
    private String packageName;
    private Integer contactTerm;
    private Double dailyProfit;
    private Double totalProfit;
    private String settleInterestTime;
    private Double level1Bonus;
    private Double level2Bonus;
    private Double level3Bonus;
    private MultipartFile image;
}
