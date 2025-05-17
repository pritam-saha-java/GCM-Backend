package com.gcm.backend.service.admin.implementation;

import com.gcm.backend.entity.MessageEntity;
import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.repository.CoinsRepository;
import com.gcm.backend.repository.MessageRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.repository.WithdrawRepository;
import com.gcm.backend.service.admin.AdminWithdrawService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminWithdrawServiceImpl implements AdminWithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final CoinsRepository coinsRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public AdminWithdrawServiceImpl(WithdrawRepository withdrawRepository,
                                    CoinsRepository coinsRepository,
                                    UserRepository userRepository,
                                    MessageRepository messageRepository) {
        this.withdrawRepository = withdrawRepository;
        this.coinsRepository = coinsRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<WithdrawEntity> getAllWithdrawals() {
        return withdrawRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<WithdrawEntity> getWithdrawalsByUser(String userName) {
        return withdrawRepository.findByUserNameOrderByCreatedAtDesc(userName);
    }

    @Override
    public WithdrawEntity getWithdrawById(Long id) {
        return withdrawRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Withdraw not found"));
    }

    @Override
    public WithdrawEntity updateWithdrawStatus(Long id, DespositStatusEnum newStatus) {
        WithdrawEntity withdraw = getWithdrawById(id);
        withdraw.setStatus(newStatus);
        withdraw.setUpdatedAt(LocalDateTime.now());

        if (newStatus.equals(DespositStatusEnum.Approved)) {
            userRepository.findByUsername(withdraw.getUserName())
                    .ifPresent(user -> {
                        Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
                        double amountToDeduct = withdraw.getAmountInUsd();
                        user.setBalance(currentBalance - amountToDeduct);
                        userRepository.save(user);

                        MessageEntity withdrawMsg = new MessageEntity();
                        withdrawMsg.setUserName(user.getUsername());
                        withdrawMsg.setMessage(amountToDeduct + " USD debited from your account for approved withdrawal.");
                        messageRepository.save(withdrawMsg);
                    });
        }

        return withdrawRepository.save(withdraw);
    }

}
