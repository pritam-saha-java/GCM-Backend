package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.DepositEntity;
import com.gcm.backend.entity.MessageEntity;
import com.gcm.backend.entity.User;
import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.repository.DepositRepository;
import com.gcm.backend.repository.MessageRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.repository.WithdrawRepository;
import com.gcm.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WithdrawRepository withdrawRepository;
    private final DepositRepository depositRepository;
    private final MessageRepository messageRepository;

    public UserServiceImpl(UserRepository userRepository,
                           WithdrawRepository withdrawRepository,
                           DepositRepository depositRepository,
                           MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.withdrawRepository = withdrawRepository;
        this.depositRepository = depositRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Map<String, Object> getUserBalance(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        return Map.of("balance", user.getBalance());
    }

    @Override
    public List<Map<String, Object>> getUserTransactionHistory(String userName) {
        List<Map<String, Object>> transactions = new ArrayList<>();

        List<DepositEntity> deposits = depositRepository.findByUserNameAndStatus(userName, DespositStatusEnum.Approved);
        for (DepositEntity deposit : deposits) {
            transactions.add(Map.of(
                    "txid", deposit.getTxId(),
                    "date", deposit.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                    "amount", "+" + deposit.getUsdAmount(),
                    "walletType", "Deposit",
                    "currency", "USD"
            ));
        }

        List<WithdrawEntity> withdrawals = withdrawRepository.findByUserNameAndStatus(userName, DespositStatusEnum.Approved);
        for (WithdrawEntity withdraw : withdrawals) {
            transactions.add(Map.of(
                    "txid", withdraw.getTxId(),
                    "date", withdraw.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                    "amount", "-" + withdraw.getAmountInUsd(),
                    "walletType", "Withdrawal",
                    "currency", "USD"
            ));
        }

        transactions.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));

        return transactions;
    }

    @Override
    public List<String> getUserMessages(String userName){
        List<MessageEntity> messages = messageRepository.findByUserName(userName);
        if (messages.isEmpty()){
            return Collections.emptyList();
        } else {
            return messages
                    .stream()
                    .map(MessageEntity::getMessage).toList();
        }
    }
}
