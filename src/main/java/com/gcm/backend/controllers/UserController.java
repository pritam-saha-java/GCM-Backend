package com.gcm.backend.controllers;

import com.gcm.backend.entity.User;
import com.gcm.backend.payload.request.ChangePasswordRequest;
import com.gcm.backend.payload.response.MessageResponse;
import com.gcm.backend.payload.response.UserAffiliateResponse;
import com.gcm.backend.payload.response.UserDashBoardResponse;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.AffiliateService;
import com.gcm.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final AffiliateService affiliateService;

    public UserController(UserService userService,
                          PasswordEncoder encoder,
                          UserRepository userRepository,
                          AffiliateService affiliateService) {
        this.userService = userService;
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.affiliateService = affiliateService;
    }

    @RequestMapping(path = "/get-balance", method = RequestMethod.GET)
    public ResponseEntity<?> registerUser(@RequestParam String userName) {
        try {
            Map<String, Object> balance = userService.getUserBalance(userName);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public ResponseEntity<?> getUserTransactions(@RequestParam String username) {
        try {
        List<Map<String, Object>> history = userService.getUserTransactionHistory(username);
        return new ResponseEntity<>(history, HttpStatus.OK);
    } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/get-user-affiliate-data", method = RequestMethod.GET)
    public ResponseEntity<?> getUserAffiliateData(@RequestParam String username) {
        try {
            UserAffiliateResponse userAffiliateData = affiliateService.getUserAffiliateData(username);
            return new ResponseEntity<>(userAffiliateData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/get-messages", method = RequestMethod.GET)
    public ResponseEntity<?> getUserMessages(@RequestParam String username) {
        try {
            List<String> response = userService.getUserMessages(username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/change-login-password", method = RequestMethod.PUT)
    public ResponseEntity<?> changeLoginPassword(@RequestBody ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
        }

        User user = userOpt.get();

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Old login password is incorrect."));
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setRawPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Login password changed successfully."));
    }

    @RequestMapping(path = "/change-payment-password", method = RequestMethod.PUT)
    public ResponseEntity<?> changePaymentPassword(@RequestBody ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
        }

        User user = userOpt.get();

        if (!encoder.matches(request.getOldPassword(), user.getPaymentPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Old payment password is incorrect."));
        }

        user.setPaymentPassword(encoder.encode(request.getNewPassword()));
        user.setRawPaymentPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Payment password changed successfully."));
    }

    @RequestMapping(path = "/user-dashboard", method = RequestMethod.GET)
    public ResponseEntity<?> getUserStats(@RequestParam String username) {
        try {
            UserDashBoardResponse userStats = userService.getUserStats(username);
            return new ResponseEntity<>(userStats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
