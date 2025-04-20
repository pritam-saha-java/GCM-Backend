package com.gcm.backend.controllers;

import com.gcm.backend.entity.CoinsEntity;
import com.gcm.backend.payload.request.BindWalletRequest;
import com.gcm.backend.payload.request.DepositRequest;
import com.gcm.backend.payload.response.DepositResponse;
import com.gcm.backend.payload.response.HistoricalDepositResponse;
import com.gcm.backend.service.DepositService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deposit")
public class DepositController {
    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @RequestMapping(path = "/get-all-coins", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCoins() {
        try {
            List<CoinsEntity> coins = depositService.getAllCoins();
            return new ResponseEntity<>(coins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/submit-deposit", method = RequestMethod.POST)
    public ResponseEntity<?> submitDeposit(@RequestParam String userName,
                                           @ModelAttribute DepositRequest depositRequest) {
        try {
            DepositResponse response = depositService.submitDeposit(depositRequest, userName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/get-historical-deposits", method = RequestMethod.GET)
    public ResponseEntity<?> getUserHistoricalDeposits(@RequestParam String userName) {
        try {
            List<HistoricalDepositResponse> deposits = depositService.getUserHistoricalDeposits(userName);
            return new ResponseEntity<>(deposits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/bind-user-account-to-currency", method = RequestMethod.POST)
    public ResponseEntity<?> bindUserAccountToCurrency(@ModelAttribute BindWalletRequest request) {
        try {
            Boolean account = depositService.bindUserAccountToCurrency(request);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
