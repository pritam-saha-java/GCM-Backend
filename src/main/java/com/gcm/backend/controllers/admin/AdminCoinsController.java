package com.gcm.backend.controllers.admin;

import com.gcm.backend.entity.CoinsEntity;
import com.gcm.backend.service.admin.AdminCoinsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/coins")
public class AdminCoinsController {

    private final AdminCoinsService adminCoinsService;

    public AdminCoinsController(AdminCoinsService adminCoinsService) {
        this.adminCoinsService = adminCoinsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> addCoin(@RequestBody CoinsEntity coin) {
        try {
            CoinsEntity saved = adminCoinsService.addCoin(coin);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCoin(@PathVariable Long id, @RequestBody CoinsEntity coin) {
        try {
            CoinsEntity updated = adminCoinsService.updateCoin(id, coin);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCoin(@PathVariable Long id) {
        try {
            adminCoinsService.deleteCoin(id);
            return new ResponseEntity<>(Map.of("message", "Coin deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCoin(@PathVariable Long id) {
        try {
            CoinsEntity coin = adminCoinsService.getCoin(id);
            return new ResponseEntity<>(coin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get-all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCoins() {
        try {
            List<CoinsEntity> coins = adminCoinsService.getAllCoins();
            return new ResponseEntity<>(coins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

