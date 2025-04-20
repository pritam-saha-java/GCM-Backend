package com.gcm.backend.service.implementation;

import com.gcm.backend.entity.User;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> getUserBalance(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        return Map.of("balance", user.getBalance());
    }
}
