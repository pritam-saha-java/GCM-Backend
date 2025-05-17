package com.gcm.backend.service.admin;

import com.gcm.backend.entity.PackagesEntity;
import com.gcm.backend.payload.request.CreatePackageRequest;
import com.gcm.backend.payload.response.AdminUserPackageResponse;
import com.gcm.backend.payload.response.PackageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminPackageService {
    ResponseEntity<String> createPackage(CreatePackageRequest request);

    ResponseEntity<String> updatePackage(Long id, CreatePackageRequest request);

    ResponseEntity<String> deletePackage(Long id);

    List<PackageResponse> getAllPackages();

    List<AdminUserPackageResponse> getAllUserPackages();
}
