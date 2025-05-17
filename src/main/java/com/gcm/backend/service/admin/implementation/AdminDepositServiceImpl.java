package com.gcm.backend.service.admin.implementation;

import com.gcm.backend.entity.DepositEntity;
import com.gcm.backend.entity.MessageEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.repository.DepositRepository;
import com.gcm.backend.repository.MessageRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.admin.AdminDepositService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDepositServiceImpl implements AdminDepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public AdminDepositServiceImpl(DepositRepository depositRepository,
                                   UserRepository userRepository,
                                   MessageRepository messageRepository) {
        this.depositRepository = depositRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<DepositEntity> getAllDeposits() {
        return (List<DepositEntity>) depositRepository.findAll();
    }

    @Override
    public DepositEntity getDepositById(Long id) {
        return depositRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found with ID: " + id));
    }

    @Override
    public DepositEntity changeDepositStatus(Long id, DespositStatusEnum newStatus) {
        DepositEntity deposit = getDepositById(id);
        deposit.setStatus(newStatus);
        deposit.setUpdatedAt(java.time.LocalDateTime.now());

        if (newStatus.equals(DespositStatusEnum.Approved)) {
            userRepository.findByUsername(deposit.getUserName())
                    .ifPresent(user -> {
                        Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
                        user.setBalance(currentBalance + deposit.getUsdAmount());
                        userRepository.save(user);

                        MessageEntity depositMsg = new MessageEntity();
                        depositMsg.setUserName(user.getUsername());
                        depositMsg.setMessage(deposit.getUsdAmount() + " USD credited to your account from approved deposit.");
                        messageRepository.save(depositMsg);
                    });
        }

        return depositRepository.save(deposit);
    }

}

