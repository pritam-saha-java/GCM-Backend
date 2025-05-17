package com.gcm.backend.service.admin;

import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.payload.request.WithdrawRequest;

import java.util.List;

public interface AdminWithdrawService {
    List<WithdrawEntity> getAllWithdrawals();
    List<WithdrawEntity> getWithdrawalsByUser(String userName);
    WithdrawEntity getWithdrawById(Long id);
    WithdrawEntity updateWithdrawStatus(Long id, DespositStatusEnum newStatus);
}
