package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class UserPackagesResponse {
    private String txId;
    private String purchaseTime;
    private String packageName;
    private String totalProfit;
    private Integer contactTerm;
    private Integer amount;
    private String nextPay;
}
