package com.gcm.backend.payload.response;

import lombok.Data;

@Data
public class PurchasePackageRequest {
    private String userName;
    private Long packageId;
    public Integer quantity;
    private String PaymentPassword;
}
