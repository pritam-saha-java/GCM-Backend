package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.User;
import com.gcm.backend.payload.response.UserAffiliateResponse;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.AffiliateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffiliateServiceImpl implements AffiliateService {
    public final UserRepository userRepository;

    public AffiliateServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserAffiliateResponse getUserAffiliateData(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserAffiliateResponse response = new UserAffiliateResponse();
        response.setReferralCode(user.getUserReferralCode());

        List<User> referralCode = userRepository.findByReferralCode(user.getUserReferralCode());
        if (!referralCode.isEmpty()) {
            List<String> names = referralCode.stream().map(User::getUsername).toList();
            response.setPartnerList(names);
        }
        return response;
    }
}
