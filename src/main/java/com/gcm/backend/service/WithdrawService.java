package com.gcm.backend.service;

import com.gcm.backend.payload.request.WithdrawRequest;
import com.gcm.backend.payload.response.HistoricalWithdrawResponse;
import com.gcm.backend.payload.response.PaymentResponse;
import com.gcm.backend.payload.response.WithdrawMethodsResponse;

import java.util.List;

public interface WithdrawService {
    List<WithdrawMethodsResponse> getWithdrawMethod(String userName);

    PaymentResponse submitWithdrawRequest(WithdrawRequest request, String userName);

    List<HistoricalWithdrawResponse> getUserHistoricalWithdraws(String userName);
}
