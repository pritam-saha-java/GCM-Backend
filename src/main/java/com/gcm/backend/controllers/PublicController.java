package com.gcm.backend.controllers;

import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.service.PackagesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PackagesService packagesService;

    public PublicController(PackagesService packagesService) {
        this.packagesService = packagesService;
    }

    @RequestMapping(path = "/all-packages", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPackages() {
        try {
            List<PackageResponse> allPackages = packagesService.getAllPackages();
            return new ResponseEntity<>(allPackages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
