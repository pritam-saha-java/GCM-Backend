package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class HistoricalDepositResponse {
    private String txId;
    private String coinType;
    private Double amount;
    private String status;
    private String time;
    private Double usdAmount;
}
