package com.gcm.backend.repository;

import com.gcm.backend.entity.UserPackagesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPackagesRepository extends CrudRepository<UserPackagesEntity, Long> {
    List<UserPackagesEntity> findByUserName(String userName);
}
