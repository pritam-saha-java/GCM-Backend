package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.*;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.payload.response.UserDashBoardResponse;
import com.gcm.backend.repository.*;
import com.gcm.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WithdrawRepository withdrawRepository;
    private final DepositRepository depositRepository;
    private final MessageRepository messageRepository;
    private final UserPackagesRepository userPackagesRepository;
    private final PackagesRepository packagesRepository;

    public UserServiceImpl(UserRepository userRepository,
                           WithdrawRepository withdrawRepository,
                           DepositRepository depositRepository,
                           MessageRepository messageRepository,
                           UserPackagesRepository userPackagesRepository,
                           PackagesRepository packagesRepository) {
        this.userRepository = userRepository;
        this.withdrawRepository = withdrawRepository;
        this.depositRepository = depositRepository;
        this.messageRepository = messageRepository;
        this.userPackagesRepository = userPackagesRepository;
        this.packagesRepository = packagesRepository;
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
    public List<String> getUserMessages(String userName) {
        List<MessageEntity> messages = messageRepository.findByUserName(userName);
        if (messages.isEmpty()){
            return Collections.emptyList();
        } else {
            return messages
                    .stream()
                    .map(MessageEntity::getMessage).toList();
        }
    }

    @Override
    public UserDashBoardResponse getUserStats(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserDashBoardResponse response = new UserDashBoardResponse();

        List<UserPackagesEntity> userPackages = userPackagesRepository.findByUserName(userName);
        int totalOrders = userPackages.size();
        int activeOrders = (int) userPackages.stream()
                .filter(p -> p.getCompletionTime() != null && p.getCompletionTime().isAfter(LocalDateTime.now()))
                .count();
        int expiredOrders = totalOrders - activeOrders;

        double totalProfit = userPackages.stream()
                .mapToDouble(up -> {
                    Optional<PackagesEntity> pkgOpt = packagesRepository.findById(up.getPackageId());
                    if (pkgOpt.isPresent()) {
                        PackagesEntity pkg = pkgOpt.get();
                        return (pkg.getTotalProfit() - pkg.getContactPrice()) * up.getQuantity();
                    }
                    return 0.0;
                })
                .sum();

        response.setTotalProfit(String.valueOf(totalProfit));
        response.setTotalOrders(String.valueOf(totalOrders));
        response.setActiveOrders(String.valueOf(activeOrders));
        response.setExpiredOrders(String.valueOf(expiredOrders));

        response.setTotalCommission(user.getCommission().toString());

        double deposit = depositRepository
                .findByUserNameAndStatus(user.getUsername(), DespositStatusEnum.Approved)
                .stream()
                .mapToDouble(DepositEntity::getUsdAmount)
                .sum();
        response.setTotalDeposit(String.valueOf(deposit));

        double withdraw = withdrawRepository
                .findByUserNameAndStatus(user.getUsername(), DespositStatusEnum.Approved)
                .stream()
                .mapToDouble(WithdrawEntity::getAmountInUsd)
                .sum();
        response.setTotalWithdrawals(Double.toString(withdraw));

        response.setAvailableBalance(user.getBalance().toString());
        response.setReferralCode(user.getUserReferralCode());
        return response;
    }
}
