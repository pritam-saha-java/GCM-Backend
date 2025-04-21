package com.gcm.backend.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class UserAffiliateResponse {
    private String referralCode;
    private List<String> partnerList;
}
