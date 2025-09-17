package com.finq.controllers;

import com.finq.dtos.requests.AdminLoginRequest;
import com.finq.dtos.requests.UpdateKycRequest;
import com.finq.dtos.responses.BaseApiResponse;
import com.finq.entities.AdminUser;
import com.finq.entities.User;
import com.finq.enums.KycStatus;
import com.finq.enums.Status;
import com.finq.repositories.AdminUserRepository;
import com.finq.repositories.UserRepository;
import com.finq.services.AuthService;
import com.finq.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Tag(name = "Admin Operations", description = "Admin panel endpoints for user and system management")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get dashboard statistics", description = "Get comprehensive dashboard statistics for admin panel")
    @ApiResponses(value = {@ApiResponse(responseCode = "401", description = "Unauthorized"), @ApiResponse(responseCode = "403", description = "Access denied")})
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BANK_MANAGER', 'CUSTOMER_SERVICE')")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // User statistics
            stats.put("totalUsers", userService.getTotalUserCount());
            stats.put("activeUsers", userService.getActiveUserCount());
            stats.put("verifiedUsers", userService.getVerifiedUserCount());
            stats.put("pendingKyc", userService.getPendingKycCount());
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long recentRegistrations = userRepository.countByCreationDateAfter(thirtyDaysAgo);
            stats.put("recentRegistrations", recentRegistrations);

            // KYC statistics
            Map<String, Long> kycStats = new HashMap<>();
            kycStats.put("pending", userRepository.countByKycStatus(KycStatus.PENDING));
            kycStats.put("verified", userRepository.countByKycStatus(KycStatus.VERIFIED));
            kycStats.put("rejected", userRepository.countByKycStatus(KycStatus.REJECTED));
            stats.put("kycBreakdown", kycStats);
            Map<String, Long> statusStats = new HashMap<>();
            statusStats.put("active", userRepository.countByStatus(Status.ACTIVE));
            statusStats.put("suspended", userRepository.countByStatus(Status.SUSPENDED));
            statusStats.put("locked", userRepository.countByStatus(Status.CLOSED));
            stats.put("accountStatusBreakdown", statusStats);

            return ResponseEntity.ok(Map.of("stats", stats));
        } catch (Exception e) {
            logger.error("Error fetching dashboard statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch dashboard statistics"));
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user details", description = "Get detailed information about a specific user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BANK_MANAGER', 'CUSTOMER_SERVICE', 'KYC_OFFICER')")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
            return ResponseEntity.ok(new BaseApiResponse<>(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching user details for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch user details"));
        }
    }

    @PutMapping("/users/{userId}/kyc")
    @PreAuthorize("hasRole('SUPER_ADMIN', 'KYC_OFFICER', 'BANK_MANAGER')")
    public ResponseEntity<?> updateKycStatus(@PathVariable UUID userId, @Valid @RequestBody UpdateKycRequest request) {
        try {
            userService.updateKycStatus(userId, request.getKycStatus());

            return ResponseEntity.ok(Map.of("message", "KYC status updated successfully", "userId", userId, "newKycStatus", request.getKycStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating KYC status for ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update KYC status"));
        }
    }

}
