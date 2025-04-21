package com.gcm.backend.repository;

import com.gcm.backend.entity.PackagesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagesRepository extends CrudRepository<PackagesEntity, Long> {

}
