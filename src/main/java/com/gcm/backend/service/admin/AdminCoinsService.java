package com.gcm.backend.service.admin;

import com.gcm.backend.entity.CoinsEntity;

import java.util.List;

public interface AdminCoinsService {
    CoinsEntity addCoin(CoinsEntity coin);

    CoinsEntity updateCoin(Long id, CoinsEntity updatedCoin);

    void deleteCoin(Long id);

    CoinsEntity getCoin(Long id);

    List<CoinsEntity> getAllCoins();
}
