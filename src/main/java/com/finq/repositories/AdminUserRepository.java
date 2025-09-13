package com.finq.repositories;

import com.finq.entities.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
    Optional<AdminUser> findByEmail(String email);

    Optional<AdminUser> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    @Query("SELECT a from AdminUser a WHERE a.email = :email AND a.status='ACTIVE'")
    Optional<AdminUser> findActiveAdminByEmail(String email);

    @Query("SELECT a FROM AdminUser a JOIN FETCH a.role r JOIN FETCH r.permissions WHERE a.email = :email")
    Optional<AdminUser> findByEmailWithRoleAndPermissions(String email);

    @Query("SELECT a FROM AdminUser a WHERE a.employeeId = :employeeId AND a.status = 'ACTIVE'")
    Optional<AdminUser> findActiveAdminByEmployeeId(String id);

    @Query("SELECT a FROM AdminUser a WHERE a.role.roleCode = :roleCode AND a.status = 'ACTIVE'")
    List<AdminUser> findByRoleCode(@Param("roleCode") String roleCode);

    @Query("SELECT a FROM AdminUser a WHERE a.branchId = :branchId AND a.status = 'ACTIVE'")
    List<AdminUser> findByBranchId(@Param("branchId") UUID branchId);

    @Query("SELECT a FROM AdminUser a WHERE a.manager.id = :managerId AND a.status = 'ACTIVE'")
    List<AdminUser> findByManagerId(@Param("managerId") UUID managerId);

}
