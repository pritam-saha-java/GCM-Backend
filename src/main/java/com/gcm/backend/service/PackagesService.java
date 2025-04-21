package com.gcm.backend.service;

import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.payload.response.PurchasePackageRequest;
import com.gcm.backend.payload.response.UserPackagesResponse;

import java.util.List;

public interface PackagesService {
    List<PackageResponse> getAllPackages();

    Boolean purchasePackage(PurchasePackageRequest request);

    List<UserPackagesResponse> getUserPackages(String userName);
}
