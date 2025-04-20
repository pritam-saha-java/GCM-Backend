package com.gcm.backend.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DepositRequest {
    private double amount;
    private double usdAmount;
    private String coinType;
    private MultipartFile screenshot;
}

