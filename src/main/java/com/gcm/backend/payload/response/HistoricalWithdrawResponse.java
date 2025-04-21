package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class HistoricalWithdrawResponse {
    private String txId;
    private String walletAddress;
    private Double amount;
    private String status;
    private String time;
}
