package com.gcm.backend.service;

import com.gcm.backend.payload.response.UserAffiliateResponse;

public interface AffiliateService {
    UserAffiliateResponse getUserAffiliateData(String userName);
}
