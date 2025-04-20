package com.gcm.backend.service;

import java.util.Map;

public interface UserService {
    Map<String, Object> getUserBalance(String userName);
}
