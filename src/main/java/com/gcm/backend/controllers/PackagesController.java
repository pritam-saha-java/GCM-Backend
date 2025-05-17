package com.gcm.backend.controllers;

import com.gcm.backend.entity.PackagesEntity;
import com.gcm.backend.payload.request.CreatePackageRequest;
import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.payload.response.PurchasePackageRequest;
import com.gcm.backend.payload.response.UserPackagesResponse;
import com.gcm.backend.repository.PackagesRepository;
import com.gcm.backend.service.PackagesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/packages")
public class PackagesController {

    private final PackagesRepository packagesRepository;
    private final PackagesService service;

    public PackagesController(PackagesRepository packagesRepository,
                              PackagesService service) {
        this.packagesRepository = packagesRepository;
        this.service = service;
    }

    @RequestMapping(path = "/get-all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPackages() {
        try {
            List<PackageResponse> allPackages = service.getAllPackages();
            return new ResponseEntity<>(allPackages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/purchase-package", method = RequestMethod.GET)
    public ResponseEntity<?> purchasePackage(@ModelAttribute PurchasePackageRequest request) {
        try {
            Boolean aBoolean = service.purchasePackage(request);
            return new ResponseEntity<>(aBoolean, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/get-user-packages", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPackages(@RequestParam String userName) {
        try {
            List<UserPackagesResponse> list = service.getUserPackages(userName);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("Error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPackage(@ModelAttribute CreatePackageRequest request) {
        try {
            PackagesEntity pkg = new PackagesEntity();
            pkg.setPackageName(request.getPackageName());
            pkg.setContactTerm(request.getContactTerm());
            pkg.setDailyProfit(request.getDailyProfit());
            pkg.setTotalProfit(request.getTotalProfit());
            pkg.setSettleInterestTime(request.getSettleInterestTime());
            pkg.setLevel1Bonus(request.getLevel1Bonus());
            pkg.setLevel2Bonus(request.getLevel2Bonus());
            pkg.setLevel3Bonus(request.getLevel3Bonus());

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                pkg.setImage(request.getImage().getBytes());
            }

            PackagesEntity saved = packagesRepository.save(pkg);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
