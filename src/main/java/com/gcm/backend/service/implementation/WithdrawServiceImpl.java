package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.User;
import com.gcm.backend.entity.UserCurrencyAddressEntity;
import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.payload.request.WithdrawRequest;
import com.gcm.backend.payload.response.HistoricalWithdrawResponse;
import com.gcm.backend.payload.response.PaymentResponse;
import com.gcm.backend.payload.response.WithdrawMethodsResponse;
import com.gcm.backend.repository.CoinsRepository;
import com.gcm.backend.repository.UserCurrencyAddressRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.repository.WithdrawRepository;
import com.gcm.backend.service.WithdrawService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    private final CoinsRepository coinsRepository;
    private final UserRepository userRepository;
    public final UserCurrencyAddressRepository currencyAddressRepository;
    private final WithdrawRepository withdrawRepository;


    public WithdrawServiceImpl(CoinsRepository coinsRepository,
                               UserRepository userRepository,
                               UserCurrencyAddressRepository currencyAddressRepository,
                               WithdrawRepository withdrawRepository) {
        this.coinsRepository = coinsRepository;
        this.userRepository = userRepository;
        this.currencyAddressRepository = currencyAddressRepository;
        this.withdrawRepository = withdrawRepository;
    }

    @Override
    public List<WithdrawMethodsResponse> getWithdrawMethod(String userName){
        List<UserCurrencyAddressEntity> withdraw = currencyAddressRepository.findByUserName(userName);
        if (withdraw.isEmpty()){
            return Collections.emptyList();
        } else {
            return withdraw
                    .stream()
                    .map(method -> {
                        WithdrawMethodsResponse response = new WithdrawMethodsResponse();
                        response.setId(method.getId());
                        response.setWalletAddress(method.getWalletAddress());
                        response.setCoinName(method.getCoinName());
                        coinsRepository.findById(method.getId())
                                        .ifPresent(coins ->{
                                            response.setConversionRate(coins.getConversionRate());
                                        });
                        return response;
                    }).toList();
        }
    }

    @Override
    public PaymentResponse submitWithdrawRequest(WithdrawRequest request, String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getRawPaymentPassword().equals(request.getPaymentPassword())){
            throw new IllegalArgumentException("Payment password doesn't match");
        }

        WithdrawEntity withdraw = new WithdrawEntity();
        withdraw.setAmountInUsd(request.getAmountInUsd());
        withdraw.setUserName(userName);
        withdraw.setStatus(DespositStatusEnum.Pending);
        withdraw.setSelectedCurrency(request.getWithdrawalMethod());
        withdraw.setReceivable(request.getReceivablesInCoins());

        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        withdraw.setTxId(txId);

        ZonedDateTime usaTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        withdraw.setCreatedAt(usaTime.toLocalDateTime());
        withdraw.setUpdatedAt(usaTime.toLocalDateTime());

        withdrawRepository.save(withdraw);

        return new PaymentResponse("Withdraw submitted successfully", withdraw.getId());
    }

    @Override
    public List<HistoricalWithdrawResponse> getUserHistoricalWithdraws(String userName) {
        List<WithdrawEntity> withdraw = withdrawRepository.findByUserNameOrderByCreatedAtDesc(userName);
        if (withdraw.isEmpty()) {
            return Collections.emptyList();
        } else {
            return withdraw.stream()
                    .map(entity -> {
                        HistoricalWithdrawResponse response = new HistoricalWithdrawResponse();
                        response.setTxId(entity.getTxId());
                        response.setAmount(entity.getAmountInUsd());
                        response.setStatus(String.valueOf(entity.getStatus()));
                        response.setTime(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                        currencyAddressRepository.findByUserNameAndCoinsId(userName, entity.getSelectedCurrency())
                                .ifPresent(currency -> {
                                    response.setWalletAddress(maskWalletAddress(currency.getWalletAddress()));
                                });
                        return response;
                    }).toList();
        }
    }

    private String maskWalletAddress(String address) {
        if (address != null && address.length() >= 6) {
            return address.substring(0, 3) + "***" + address.substring(address.length() - 3);
        } else {
            return "***";
        }
    }

}
