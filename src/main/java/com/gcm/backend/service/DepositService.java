package com.gcm.backend.service;

import com.gcm.backend.entity.CoinsEntity;
import com.gcm.backend.payload.request.BindWalletRequest;
import com.gcm.backend.payload.request.DepositRequest;
import com.gcm.backend.payload.response.PaymentResponse;
import com.gcm.backend.payload.response.HistoricalDepositResponse;

import java.util.List;

public interface DepositService {
    List<CoinsEntity> getAllCoins();

    PaymentResponse submitDeposit(DepositRequest request, String userName);

    List<HistoricalDepositResponse> getUserHistoricalDeposits(String userName);

    Boolean bindUserAccountToCurrency(BindWalletRequest request);
}
