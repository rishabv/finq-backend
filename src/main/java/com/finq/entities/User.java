package com.finq.entities;


import com.finq.enums.Gender;
import com.finq.enums.KycStatus;
import com.finq.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_phone", columnList = "phone"),
        @Index(name = "idx_users_customer_id", columnList = "customer_id"),
        @Index(name = "idx_users_status", columnList = "status")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name="first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name="email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name="customer_id", unique = true, nullable = false, length = 10)
    private String customerId;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "adhaar_number")
    private String adhaarNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private Status status = Status.INACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name="kyc_status", nullable = false)
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "is_phone_verified", nullable = false)
    private Boolean isPhoneVerified = false;

    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "last_login_at")
    private String lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalTime accountLockedUntil;

    @Column(name = "kyc_verified_at")
    private LocalTime kycVerifiedAt;

    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalTime.now());
    }
}
