package com.finq.repositories;

import com.finq.entities.User;
import com.finq.enums.KycStatus;
import com.finq.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByCustomerId(String customerId);

    Optional<User> findByPanNumber(String panNumber);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByPhone(@Param("phone") String phone);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void updateFailedLoginAttempts(@Param("userId") UUID userId, @Param("attempts") Integer attempts);

    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = :lockUntil WHERE u.id = :userId")
    void lockAccount(@Param("userId") String userId, @Param("lockUntil") LocalDateTime lockUntil);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") UUID userId, @Param("loginTime") LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE User u SET u.isEmailVerified = true WHERE u.id = :userId")
    void markEmailAsVerified(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.isPhoneVerified = true WHERE u.id = :userId")
    void markPhoneAsVerified(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.kycStatus = :status, u.kycVerifiedAt = :verifiedAt WHERE u.id = :userId")
    void updateKycStatus(@Param("userId") UUID userId,
                         @Param("status") KycStatus status,
                         @Param("verifiedAt") LocalDateTime verifiedAt);

    @Query("SELECT COUNT(u) FROM User u WHERE u.creationDate >= :startDate")
    Long countUsersCreatedSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT u FROM User u WHERE u.kycStatus=:status")
    List<User> findByKycStatus(@Param("status") KycStatus status);

    Long countByStatus(Status status);

    Long countByKycStatus(KycStatus kycStatus);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    long countByCreationDateAfter(LocalDateTime date);

    Optional<User> findFirstByEmail(String email);
}
