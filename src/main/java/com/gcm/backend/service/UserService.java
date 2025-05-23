package com.gcm.backend.service;

import com.gcm.backend.payload.response.UserDashBoardResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> getUserBalance(String userName);

    List<Map<String, Object>> getUserTransactionHistory(String userName);

    List<String> getUserMessages(String userName);

    UserDashBoardResponse getUserStats(String userName);
}
