package com.finq.repositories;

import com.finq.entities.Permission;
import com.finq.enums.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Set<Permission> findByPermissionCodeIn(Collection<PermissionCode> code);
}
