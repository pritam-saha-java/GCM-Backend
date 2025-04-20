package com.gcm.backend.payload.request;

import lombok.Data;

@Data
public class BindWalletRequest {
    private String userName;
    private Long coinsId;
    private String walletAddress;
}
