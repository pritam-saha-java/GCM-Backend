package com.gcm.backend.controllers.admin;

import com.gcm.backend.entity.DepositEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.service.admin.AdminDepositService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/deposits")
public class AdminDepositController {

    private final AdminDepositService adminDepositService;

    public AdminDepositController(AdminDepositService adminDepositService) {
        this.adminDepositService = adminDepositService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get-all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDeposits() {
        try {
            List<DepositEntity> deposits = adminDepositService.getAllDeposits();
            return new ResponseEntity<>(deposits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDeposit(@PathVariable Long id) {
        try {
            DepositEntity deposit = adminDepositService.getDepositById(id);
            return new ResponseEntity<>(deposit, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/change-status/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> changeDepositStatus(@PathVariable Long id,
                                                 @RequestParam DespositStatusEnum status) {
        try {
            DepositEntity updated = adminDepositService.changeDepositStatus(id, status);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

