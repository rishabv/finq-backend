package com.finq.services;

import com.finq.entities.User;
import com.finq.enums.KycStatus;
import com.finq.enums.Status;
import com.finq.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by customer ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findByCustomerId(String customerId) {
        return userRepository.findByCustomerId(customerId);
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

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
     * Change user password
     */
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user: {}", user.getEmail());
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

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(Status.ACTIVE);
        long pendingVerification = userRepository.countByStatus(Status.PENDING_VERIFICATION);
        long suspendedUsers = userRepository.countByStatus(Status.SUSPENDED);

        long kycVerified = userRepository.countByKycStatus(KycStatus.VERIFIED);
        long kycPending = userRepository.countByKycStatus(KycStatus.PROCESSING) +
                userRepository.countByKycStatus(KycStatus.UNDER_REVIEW);

        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        long newUsersThisWeek = userRepository.countUsersCreatedSince(lastWeek);

        return new UserStats(totalUsers, activeUsers, pendingVerification, suspendedUsers,
                kycVerified, kycPending, newUsersThisWeek);
    }

    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if user exists by phone
     */
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }



    // Inner class for user statistics
    public static class UserStats {
        private final long totalUsers;
        private final long activeUsers;
        private final long pendingVerification;
        private final long suspendedUsers;
        private final long kycVerified;
        private final long kycPending;
        private final long newUsersThisWeek;

        public UserStats(long totalUsers, long activeUsers, long pendingVerification,
                         long suspendedUsers, long kycVerified, long kycPending, long newUsersThisWeek) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.pendingVerification = pendingVerification;
            this.suspendedUsers = suspendedUsers;
            this.kycVerified = kycVerified;
            this.kycPending = kycPending;
            this.newUsersThisWeek = newUsersThisWeek;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getPendingVerification() { return pendingVerification; }
        public long getSuspendedUsers() { return suspendedUsers; }
        public long getKycVerified() { return kycVerified; }
        public long getKycPending() { return kycPending; }
        public long getNewUsersThisWeek() { return newUsersThisWeek; }
    }
}