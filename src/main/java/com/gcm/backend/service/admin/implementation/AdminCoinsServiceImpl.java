package com.gcm.backend.service.admin.implementation;

import com.gcm.backend.entity.CoinsEntity;
import com.gcm.backend.repository.CoinsRepository;
import com.gcm.backend.service.admin.AdminCoinsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCoinsServiceImpl implements AdminCoinsService {

    private final CoinsRepository coinsRepository;

    public AdminCoinsServiceImpl(CoinsRepository coinsRepository) {
        this.coinsRepository = coinsRepository;
    }

    @Override
    public CoinsEntity addCoin(CoinsEntity coin) {
        return coinsRepository.save(coin);
    }

    @Override
    public CoinsEntity updateCoin(Long id, CoinsEntity updatedCoin) {
        CoinsEntity existing = coinsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coin not found with id: " + id));

        existing.setCoinName(updatedCoin.getCoinName());
        existing.setConversionRate(updatedCoin.getConversionRate());
        existing.setAdminWalletAddress(updatedCoin.getAdminWalletAddress());

        return coinsRepository.save(existing);
    }

    @Override
    public void deleteCoin(Long id) {
        if (!coinsRepository.existsById(id)) {
            throw new RuntimeException("Coin not found with id: " + id);
        }
        coinsRepository.deleteById(id);
    }

    @Override
    public CoinsEntity getCoin(Long id) {
        return coinsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coin not found with id: " + id));
    }

    @Override
    public List<CoinsEntity> getAllCoins() {
        return (List<CoinsEntity>) coinsRepository.findAll();
    }
}
