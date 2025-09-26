package com.finq.entities;

import com.finq.enums.PermissionCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="permissions", indexes = {
        @Index(name="idx_permission_code", columnList = "permission_code"),
        @Index(name="idx_permissions_resource_action", columnList = "resource, action")
})
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {
    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 50, message = "Permission name must be between 2 and 50 characters")
    @Column(name = "permission_name", unique = true, nullable = false, length = 50)
    private String permissionName;

    @NotBlank(message = "Permission code is required")
    @Size(min = 2, max = 50, message = "Permission code must be between 2 and 50 characters")
    @Column(name = "permission_code", unique = true, nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PermissionCode permissionCode;

    @NotBlank(message = "Resource is required")
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @NotBlank(message = "Action is required")
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    public Permission(String permissionName, PermissionCode permissionCode, String resource, String action, String description) {
        this.permissionName = permissionName;
        this.permissionCode = permissionCode;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    public static class Actions {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String APPROVE = "approve";
        public static final String REJECT = "reject";
        public static final String EXPORT = "export";
        public static final String IMPORT = "import";
    }

    public static class Resources {
        public static final String USERS = "users";
        public static final String ACCOUNTS = "accounts";
        public static final String TRANSACTIONS = "transactions";
        public static final String CARDS = "cards";
        public static final String LOANS = "loans";
        public static final String REPORTS = "reports";
        public static final String SYSTEM_CONFIG = "system_config";
        public static final String FRAUD_MANAGEMENT = "fraud_management";
        public static final String COMPLIANCE = "compliance";
        public static final String ADMIN_USERS = "admin_users";
    }
}
