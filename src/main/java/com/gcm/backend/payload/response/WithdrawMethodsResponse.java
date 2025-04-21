package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class WithdrawMethodsResponse {
    private Long id;
    private String coinName;
    private String walletAddress;
    private Double conversionRate;
}
