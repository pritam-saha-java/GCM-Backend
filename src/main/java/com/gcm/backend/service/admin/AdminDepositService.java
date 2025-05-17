package com.gcm.backend.service.admin;

import com.gcm.backend.entity.DepositEntity;
import com.gcm.backend.enums.DespositStatusEnum;

import java.util.List;

public interface AdminDepositService {
    List<DepositEntity> getAllDeposits();
    DepositEntity getDepositById(Long id);
    DepositEntity changeDepositStatus(Long id, DespositStatusEnum newStatus);
}

