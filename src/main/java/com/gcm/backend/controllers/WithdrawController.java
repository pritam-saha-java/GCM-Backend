package com.gcm.backend.controllers;

import com.gcm.backend.payload.request.WithdrawRequest;
import com.gcm.backend.payload.response.HistoricalDepositResponse;
import com.gcm.backend.payload.response.HistoricalWithdrawResponse;
import com.gcm.backend.payload.response.PaymentResponse;
import com.gcm.backend.payload.response.WithdrawMethodsResponse;
import com.gcm.backend.service.WithdrawService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {
    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @RequestMapping(path = "/get-withdraw-method", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCoins(@RequestParam String userName) {
        try {
            List<WithdrawMethodsResponse> withdrawMethod = withdrawService.getWithdrawMethod(userName);
            return new ResponseEntity<>(withdrawMethod, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/submit-withdraw", method = RequestMethod.POST)
    public ResponseEntity<?> submitWithdrawRequest(@RequestParam String userName,
                                                   @ModelAttribute WithdrawRequest request) {
        try {
            PaymentResponse response = withdrawService.submitWithdrawRequest(request, userName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/get-historical-withdraws", method = RequestMethod.GET)
    public ResponseEntity<?> getUserHistoricalWithdraws(@RequestParam String userName) {
        try {
            List<HistoricalWithdrawResponse> responses = withdrawService.getUserHistoricalWithdraws(userName);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
