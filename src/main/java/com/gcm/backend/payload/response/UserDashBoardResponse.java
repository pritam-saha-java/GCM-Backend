package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class UserDashBoardResponse {
    private String availableBalance;
    private String totalProfit;
    private String totalCommission;
    private String activeOrders;
    private String expiredOrders;
    private String totalOrders;
    private String totalDeposit;
    private String totalWithdrawals;
    private String referralCode;
}
