package com.finq.services;

import com.finq.dtos.requests.CreateUserRequest;
import com.finq.dtos.responses.BaseApiResponse;
import com.finq.entities.AdminUser;
import com.finq.entities.User;
import com.finq.enums.KycStatus;
import com.finq.enums.Status;
import com.finq.repositories.UserRepository;
import com.finq.utils.Utils;
import com.finq.utils.VerificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationUtils verificationUtils;

    /**
     * Find users by KYC status
     */
    @Transactional(readOnly = true)
    public List<User> findByKycStatus(KycStatus status) {
        return userRepository.findByKycStatus(status);
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public long getActiveUserCount() {
        return userRepository.countByStatus(Status.ACTIVE);
    }

    public long getVerifiedUserCount() {
        return userRepository.countByKycStatus(KycStatus.VERIFIED);
    }

    public long getPendingKycCount() {
        return userRepository.countByKycStatus(KycStatus.PENDING);
    }

    /**
     * Update user profile
     */
    public User updateProfile(UUID userId, String firstName, String lastName, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);

        User updatedUser = userRepository.save(user);
        logger.info("User profile updated: {}", user.getEmail());

        return updatedUser;
    }


    /**
     * Verify email
     */
    public void verifyEmail(UUID userId) {
        userRepository.markEmailAsVerified(userId);
        logger.info("Email verified for user ID: {}", userId);
    }

    /**
     * Verify phone
     */
    public void verifyPhone(UUID userId) {
        userRepository.markPhoneAsVerified(userId);
        logger.info("Phone verified for user ID: {}", userId);
    }

    /**
     * Update KYC status
     */
    public void updateKycStatus(UUID userId, KycStatus status) {
        LocalDateTime verifiedAt = (status == KycStatus.VERIFIED) ? LocalDateTime.now() : null;
        userRepository.updateKycStatus(userId, status, verifiedAt);
        logger.info("KYC status updated to {} for user ID: {}", status, userId);
    }

    /**
     * Suspend user account
     */
    public void suspendUser(UUID userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(Status.SUSPENDED);
        userRepository.save(user);

        logger.warn("User suspended: {} - Reason: {}", user.getEmail(), reason);
    }

    /**
     * Activate user account
     */
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        logger.info("User activated: {}", user.getEmail());
    }

    /**
     * Close user account
     */
    public void closeAccount(UUID userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(Status.CLOSED);
        userRepository.save(user);

        logger.warn("User account closed: {} - Reason: {}", user.getEmail(), reason);
    }

    public boolean validateExistence(String adhaar, String pan) {
        boolean adhaarExists = userRepository.existsByAdhaarNumber(adhaar);
        boolean panExists = userRepository.existsByPanNumber(pan);
        if(adhaarExists || panExists) throw new IllegalArgumentException("Details already exists");
        return true;
    }

    public User createCustomer(CreateUserRequest request) {
        validateExistence(request.getAadhaarNumber(), request.getPanNumber());
        CompletableFuture<Boolean> adhaarFuture = verificationUtils.validateAdhaar(request.getAadhaarNumber());
        CompletableFuture<Boolean> panFuture = verificationUtils.validatePan(request.getPanNumber());
        CompletableFuture<Boolean> beureaFurure = verificationUtils.validateCreditBurea(request.getPanNumber());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(panFuture, adhaarFuture, beureaFurure);
        CompletableFuture<Boolean> isValidated = allFutures.thenApply((v) -> {
            boolean panValid = panFuture.join();
            boolean aadhaarValid = adhaarFuture.join();
            boolean creditValid = beureaFurure.join();
            return panValid && aadhaarValid && creditValid; //
        }).exceptionally(ex -> {
            throw new IllegalArgumentException("KYC validation failed due to: " + ex.getMessage());
        });

        if(!isValidated.join()) {
            throw new IllegalArgumentException("Details could not be verified.");
        }

        String tempPassword = Utils.generateRandomString();
        String passwordHash = passwordEncoder.encode(tempPassword);
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setStatus(Status.ONBOARDED);
        user.setPasswordHash(passwordHash);
        user.setCustomerId(Utils.generateCustomerId());
        // saving the user to DB.
        User createdUser = userRepository.save(user);
        // TODO  send email to user to create their credentials;
        System.out.println(createdUser.getCustomerId() + " " + createdUser.getEmail() + " " + tempPassword);
        return createdUser;
    }

    public AdminUser createAdminUser(CreateUserRequest request) {
        return null;
    }
}