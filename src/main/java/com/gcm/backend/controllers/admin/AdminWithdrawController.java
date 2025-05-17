package com.gcm.backend.controllers.admin;

import com.gcm.backend.entity.WithdrawEntity;
import com.gcm.backend.enums.DespositStatusEnum;
import com.gcm.backend.service.admin.AdminWithdrawService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/withdraw")
public class AdminWithdrawController {

    private final AdminWithdrawService withdrawService;

    public AdminWithdrawController(AdminWithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get-all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllWithdrawals() {
        try {
            return new ResponseEntity<>(withdrawService.getAllWithdrawals(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getWithdraw(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(withdrawService.getWithdrawById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/change-status/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWithdrawStatus(@PathVariable Long id, @RequestParam DespositStatusEnum status) {
        try {
            WithdrawEntity updated = withdrawService.updateWithdrawStatus(id, status);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

