package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.MessageEntity;
import com.gcm.backend.entity.PackagesEntity;
import com.gcm.backend.entity.User;
import com.gcm.backend.entity.UserPackagesEntity;
import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.payload.response.PurchasePackageRequest;
import com.gcm.backend.payload.response.UserPackagesResponse;
import com.gcm.backend.repository.MessageRepository;
import com.gcm.backend.repository.PackagesRepository;
import com.gcm.backend.repository.UserPackagesRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.PackagesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PackagesServiceImpl implements PackagesService {

    private final PackagesRepository packagesRepository;
    public final UserRepository userRepository;
    private final UserPackagesRepository userPackagesRepository;
    private final MessageRepository messageRepository;

    public PackagesServiceImpl(PackagesRepository packagesRepository,
                               UserRepository userRepository,
                               UserPackagesRepository userPackagesRepository,
                               MessageRepository messageRepository) {
        this.packagesRepository = packagesRepository;
        this.userRepository = userRepository;
        this.userPackagesRepository = userPackagesRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<PackageResponse> getAllPackages() {
        List<PackagesEntity> packages = (List<PackagesEntity>) packagesRepository.findAll();

        return packages.stream().map(pkg -> {
            PackageResponse res = new PackageResponse();
            res.setId(pkg.getId());
            res.setPackageName(pkg.getPackageName());
            res.setContactTerm(pkg.getContactTerm());
            res.setDailyProfit(pkg.getDailyProfit());
            res.setTotalProfit(pkg.getTotalProfit());
            res.setSettleInterestTime(pkg.getSettleInterestTime());
            res.setLevel1Bonus(pkg.getLevel1Bonus());
            res.setLevel2Bonus(pkg.getLevel2Bonus());
            res.setLevel3Bonus(pkg.getLevel3Bonus());
            res.setContactPrice(pkg.getContactPrice());

            if (pkg.getImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(pkg.getImage());
                res.setImageBase64(base64Image);
            }

            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean purchasePackage(PurchasePackageRequest request) {
        log.info(request.toString());
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getRawPaymentPassword().equals(request.getPaymentPassword())){
            throw new RuntimeException("Payment Password doesn't match");
        }

        PackagesEntity packages = packagesRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found"));
        int contactTermInHours = packages.getContactTerm() * 24;

        UserPackagesEntity entity = new UserPackagesEntity();
        entity.setPackageId(request.getPackageId());
        entity.setUserName(request.getUserName());
        entity.setQuantity(request.getQuantity());
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        entity.setTxId(txId);
        ZonedDateTime usaTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        entity.setCompletionTime(usaTime.plusHours(contactTermInHours).toLocalDateTime());
        entity.setCreatedAt(usaTime.toLocalDateTime());

        // SettleInterestTime is in hours (you may need to parse it from string)
        int settleInterval = Integer.parseInt(packages.getSettleInterestTime());
        entity.setNextSettleTime(usaTime.plusHours(settleInterval).toLocalDateTime());
        entity.setSettledCount(0);

        distributePackageCommission(user, packages, request.getQuantity());

        userPackagesRepository.save(entity);
        return true;
    }

    @Override
    public List<UserPackagesResponse> getUserPackages(String userName) {
        List<UserPackagesEntity> packages = userPackagesRepository.findByUserName(userName);
        if (packages.isEmpty()){
            return Collections.emptyList();
        } else {
            return packages.stream()
                    .map(pkg -> {
                        UserPackagesResponse response = new UserPackagesResponse();
                        response.setTxId(pkg.getTxId());
                        response.setPurchaseTime(pkg.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                        packagesRepository.findById(pkg.getPackageId())
                                        .ifPresent(p -> {
                                            response.setPackageName(p.getPackageName());
                                            int amount = p.getContactPrice() * pkg.getQuantity();
                                            double profit = amount * p.getDailyProfit() * p.getContactTerm();
                                            response.setTotalProfit(String.valueOf(profit));
                                            response.setContactTerm(p.getContactTerm());
                                            response.setAmount(amount);
                                        });
                        LocalDateTime now = LocalDateTime.now();
                        if (pkg.getNextSettleTime().isBefore(now) || pkg.getNextSettleTime().isEqual(now)) {
                            response.setNextPay("Completed");
                        } else {
                            response.setNextPay(pkg.getNextSettleTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                        }
                        return  response;
                    }).toList();
        }
    }

    public void distributePackageCommission(User buyer, PackagesEntity pkg, int quantity) {
        double[] commissionRates = {
                pkg.getLevel1Bonus(),
                pkg.getLevel2Bonus(),
                pkg.getLevel3Bonus()
        };

        String refCode = buyer.getReferralCode();
        int totalPrice = pkg.getContactPrice() * quantity;

        for (int level = 0; level < commissionRates.length && refCode != null && !refCode.isEmpty(); level++) {
            Optional<User> referrerOpt = userRepository.findByUserReferralCode(refCode);
            if (referrerOpt.isEmpty()) break;

            User referrer = referrerOpt.get();
            double commission = totalPrice * (commissionRates[level] / 100);

            // 💸 Add balance
            referrer.setBalance(referrer.getBalance() + commission);
            referrer.setCommission(referrer.getCommission() + commission);
            userRepository.save(referrer);

            // 📩 Notification message
            MessageEntity msg = new MessageEntity();
            msg.setUserName(referrer.getUsername());
            msg.setMessage(String.format("%.2f USD credited to your account as level %d package purchase bonus from %s",
                    commission, level + 1, buyer.getUsername()));
            messageRepository.save(msg);

            // ⬆ Move up the chain
            refCode = referrer.getReferralCode();
        }
    }


}
