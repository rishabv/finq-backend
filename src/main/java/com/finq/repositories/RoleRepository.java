package com.finq.repositories;

import com.finq.entities.Role;
import com.finq.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT r from Role r JOIN FETCH r.permissions WHERE r.id=:roleId")
    Optional<Role> findByIdWithPermissions(@Param("roleId") UUID roleId);

    Role findByRoleCode(UserRole role);
}
