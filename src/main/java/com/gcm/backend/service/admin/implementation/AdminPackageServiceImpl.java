package com.gcm.backend.service.admin.implementation;


import com.gcm.backend.entity.PackagesEntity;
import com.gcm.backend.entity.UserPackagesEntity;
import com.gcm.backend.payload.request.CreatePackageRequest;
import com.gcm.backend.payload.response.AdminUserPackageResponse;
import com.gcm.backend.payload.response.PackageResponse;
import com.gcm.backend.repository.PackagesRepository;
import com.gcm.backend.repository.UserPackagesRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.service.admin.AdminPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminPackageServiceImpl implements AdminPackageService {

    private final PackagesRepository packagesRepository;
    private final UserPackagesRepository userPackagesRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<String> createPackage(CreatePackageRequest request) {
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
            return new ResponseEntity<>("saved", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> updatePackage(Long id, CreatePackageRequest request) {
        Optional<PackagesEntity> existingOpt = packagesRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Package not found");
        }

        PackagesEntity existing = existingOpt.get();

        // Update fields from request
        existing.setPackageName(request.getPackageName());
        existing.setContactTerm(request.getContactTerm());
        existing.setDailyProfit(request.getDailyProfit());
        existing.setTotalProfit(request.getTotalProfit());
        existing.setSettleInterestTime(request.getSettleInterestTime());
        existing.setLevel1Bonus(request.getLevel1Bonus());
        existing.setLevel2Bonus(request.getLevel2Bonus());
        existing.setLevel3Bonus(request.getLevel3Bonus());

        try {
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                existing.setImage(request.getImage().getBytes());
            }

            packagesRepository.save(existing);
            return ResponseEntity.ok("Package updated successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to update image: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<String> deletePackage(Long id) {
        if (!packagesRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Package not found");
        }
        packagesRepository.deleteById(id);
        return ResponseEntity.ok("Package deleted successfully");
    }

    @Override
    public List<PackageResponse> getAllPackages() {
        return ((List<PackagesEntity>) packagesRepository.findAll()).stream().map(pkg -> {
            PackageResponse res = new PackageResponse();
            BeanUtils.copyProperties(pkg, res);
            if (pkg.getImage() != null) {
                res.setImageBase64(Base64.getEncoder().encodeToString(pkg.getImage()));
            }
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdminUserPackageResponse> getAllUserPackages() {
        List<UserPackagesEntity> entities = (List<UserPackagesEntity>) userPackagesRepository.findAll();

        return entities.stream().map(pkg -> {
            AdminUserPackageResponse res = new AdminUserPackageResponse();
            BeanUtils.copyProperties(pkg, res);

            res.setCreatedAt(pkg.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            res.setCompletionTime(pkg.getCompletionTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

            packagesRepository.findById(pkg.getPackageId()).ifPresent(packageEntity -> {
                res.setPackageName(packageEntity.getPackageName());
                res.setContactPrice(packageEntity.getContactPrice());
                res.setDailyProfit(packageEntity.getDailyProfit());
                res.setTotalProfit(packageEntity.getTotalProfit());
            });

            return res;
        }).collect(Collectors.toList());
    }
}

