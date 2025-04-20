package com.gcm.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepositResponse {
    private String message;
    private Long depositId;
}

