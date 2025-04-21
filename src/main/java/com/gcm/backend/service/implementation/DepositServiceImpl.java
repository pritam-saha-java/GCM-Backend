package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.CoinsEntity;
import com.gcm.backend.entity.DepositEntity;
import com.gcm.backend.entity.User;
import com.gcm.backend.entity.UserCurrencyAddressEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.payload.request.BindWalletRequest;
import com.gcm.backend.payload.request.DepositRequest;
import com.gcm.backend.payload.response.PaymentResponse;
import com.gcm.backend.payload.response.HistoricalDepositResponse;
import com.gcm.backend.repository.CoinsRepository;
import com.gcm.backend.repository.DepositRepository;
import com.gcm.backend.repository.UserCurrencyAddressRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.DepositService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DepositServiceImpl implements DepositService {

    private final CoinsRepository coinsRepository;
    public final DepositRepository depositRepository;
    private final UserRepository userRepository;
    public final UserCurrencyAddressRepository currencyAddressRepository;

    public DepositServiceImpl(CoinsRepository coinsRepository,
                              DepositRepository depositRepository,
                              UserRepository userRepository,
                              UserCurrencyAddressRepository currencyAddressRepository) {
        this.coinsRepository = coinsRepository;
        this.depositRepository = depositRepository;
        this.userRepository = userRepository;
        this.currencyAddressRepository = currencyAddressRepository;
    }

    @Override
    public List<CoinsEntity> getAllCoins() {
        List<CoinsEntity> response = new ArrayList<>();
        coinsRepository.findAll().forEach(response::add);
        return response;
    }

    @Override
    public PaymentResponse submitDeposit(DepositRequest request, String userName) {

        DepositEntity deposit = new DepositEntity();
        deposit.setAmount(request.getAmount());
        deposit.setCoinType(request.getCoinType());
        deposit.setUsdAmount(request.getUsdAmount());
        deposit.setUserName(userName);
        deposit.setStatus(DespositStatusEnum.Pending);

        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        deposit.setTxId(txId);

        ZonedDateTime usaTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        deposit.setCreatedAt(usaTime.toLocalDateTime());
        deposit.setUpdatedAt(usaTime.toLocalDateTime());

        try {
            deposit.setScreenshotData(request.getScreenshot().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read screenshot file", e);
        }

        depositRepository.save(deposit);

        return new PaymentResponse("Deposit submitted successfully", deposit.getId());
    }

    @Override
    public List<HistoricalDepositResponse> getUserHistoricalDeposits(String userName) {
        List<DepositEntity> deposits = depositRepository.findByUserNameOrderByCreatedAtDesc(userName);
        if (deposits.isEmpty()) {
            return Collections.emptyList();
        } else {
            return deposits.stream()
                    .map(deposit -> {
                        HistoricalDepositResponse response = new HistoricalDepositResponse();
                        response.setTxId(deposit.getTxId());
                        response.setCoinType(deposit.getCoinType());
                        response.setAmount(deposit.getAmount());
                        response.setStatus(String.valueOf(deposit.getStatus()));
                        response.setUsdAmount(deposit.getUsdAmount());
                        response.setTime(deposit.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                        return response;
                    }).toList();
        }
    }

    @Override
    public Boolean bindUserAccountToCurrency(BindWalletRequest request) {
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        CoinsEntity coins = coinsRepository.findById(request.getCoinsId())
                .orElseThrow(() -> new IllegalArgumentException("Currency Not Found"));
        Optional<UserCurrencyAddressEntity> coinsId = currencyAddressRepository.findByUserNameAndCoinsId(user.getUsername(), coins.getId());
        if (coinsId.isPresent()){
            throw new IllegalArgumentException("Currency wallet already bound");
        }

        UserCurrencyAddressEntity entity = new UserCurrencyAddressEntity();
        entity.setCoinName(coins.getCoinName());
        entity.setUserName(user.getUsername());
        entity.setCoinsId(coins.getId());
        entity.setWalletAddress(request.getWalletAddress());
        currencyAddressRepository.save(entity);
        return true;
    }

}
