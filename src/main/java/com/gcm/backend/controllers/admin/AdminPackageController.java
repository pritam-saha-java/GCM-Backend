package com.gcm.backend.controllers.admin;

import com.gcm.backend.entity.PackagesEntity;
import com.gcm.backend.payload.request.CreatePackageRequest;
import com.gcm.backend.payload.response.AdminUserPackageResponse;
import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.service.admin.AdminPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
public class AdminPackageController {

    private final AdminPackageService packageService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPackage(@ModelAttribute CreatePackageRequest request) {
        return packageService.createPackage(request);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePackage(@PathVariable Long id,
                                                @ModelAttribute CreatePackageRequest request) {
        return packageService.updatePackage(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePackage(@PathVariable Long id) {
        return packageService.deletePackage(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PackageResponse>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/user-packages")
    public ResponseEntity<List<AdminUserPackageResponse>> getAllUserPackages() {
        return ResponseEntity.ok(packageService.getAllUserPackages());
    }
}

