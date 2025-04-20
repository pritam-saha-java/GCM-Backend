package com.gcm.backend.repository;

import com.gcm.backend.entity.CoinsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinsRepository extends CrudRepository<CoinsEntity, Long> {
}
