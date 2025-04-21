package com.gcm.backend.repository;

import com.gcm.backend.entity.UserCurrencyAddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCurrencyAddressRepository extends CrudRepository<UserCurrencyAddressEntity, Long> {
    Optional<UserCurrencyAddressEntity> findByUserNameAndCoinsId(String userName, Long coinsId);
    List<UserCurrencyAddressEntity> findByUserName(String userName);
}
