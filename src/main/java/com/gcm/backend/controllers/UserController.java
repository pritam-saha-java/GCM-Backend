package com.gcm.backend.controllers;

import com.gcm.backend.payload.request.SignupRequest;
import com.gcm.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
