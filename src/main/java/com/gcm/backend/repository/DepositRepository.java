package com.gcm.backend.repository;

import com.gcm.backend.entity.DepositEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends CrudRepository<DepositEntity, Long> {

    List<DepositEntity> findByUserNameOrderByCreatedAtDesc(String userName);
}
