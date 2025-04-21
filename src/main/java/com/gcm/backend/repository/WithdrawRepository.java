package com.gcm.backend.repository;

import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRepository extends CrudRepository<WithdrawEntity, Long> {
    List<WithdrawEntity> findByUserNameOrderByCreatedAtDesc (String username);
    List<WithdrawEntity> findByUserNameAndStatus(String userName, DespositStatusEnum status);
}
