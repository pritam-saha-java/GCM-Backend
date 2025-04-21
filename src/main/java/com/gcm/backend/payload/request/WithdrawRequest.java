package com.gcm.backend.payload.request;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Double amountInUsd;
    private Long withdrawalMethod;
    private String paymentPassword;
    private Double receivablesInCoins;
}
