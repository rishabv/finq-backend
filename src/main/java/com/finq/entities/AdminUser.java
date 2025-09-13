package com.finq.entities;

import com.finq.enums.Gender;
import com.finq.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users", indexes = {
        @Index(name = "idx_admin_users_email", columnList = "email"),
        @Index(name = "idx_admin_users_employee_id", columnList = "employee_id"),
        @Index(name = "idx_admin_users_role", columnList = "role_id"),
        @Index(name = "idx_admin_users_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AdminUser extends BaseEntity {

    @Column(name = "employee_id")
    private String employeeId;

    @NotBlank(message = "First name is required")
    @Size(min=2, max = 100, message = "First name is exceeding the characters")
    @Column(name="first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min=2, max = 100, message = "Last name is exceeding the characters")
    @Column(name="last_name", nullable = false, length = 100)
    private String lastName;

    @Email(message = "Email format is not valid")
    @NotBlank(message = "Email cannot be blank")
    @Column(name="email", unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name="branch_id")
    private String branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private AdminUser manager;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    public AdminUser(String employeeId, String email, String firstName, String lastName,
                     String passwordHash, Role role) {
        this.employeeId = employeeId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
