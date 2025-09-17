package com.finq.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Cleanup;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="roles", indexes = {
        @Index(name="idx_role_code", columnList = "role_code"),
        @Index(name = "idx_role_name", columnList = "role_name")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role extends BaseEntity {
    @NotBlank(message = "Role name is required.")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @Column(name="role_name", unique = true, nullable = false, length = 100)
    private String RoleName;

    @NotBlank(message = "Role code is required")
    @Size(min = 2, max = 50, message = "Role code must be between 2 and 50 characters")
    @Column(name = "role_code", unique = true, nullable = false, length = 50)
    private String roleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private Role parentRole;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="role_permission",
            joinColumns = @JoinColumn(name="role_id"),
            inverseJoinColumns = @JoinColumn(name="permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}
