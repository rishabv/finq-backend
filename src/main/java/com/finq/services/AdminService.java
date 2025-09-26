package com.finq.services;

import com.finq.entities.Permission;
import com.finq.entities.Role;
import com.finq.entities.User;
import com.finq.enums.PermissionCode;
import com.finq.enums.UserRole;
import com.finq.repositories.PermissionRepository;
import com.finq.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminService {
    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private PermissionRepository permissionRepository;

    public Role createUserWithRoleAndPermission(UserRole roleCode, Set<PermissionCode> permissionCodes) {
        try {
        Role newRole = roleRepository.findByRoleCode(roleCode);
        Set<Permission> permissions = permissionRepository.findByPermissionCodeIn(permissionCodes);
        newRole.setPermissions(permissions);
        return newRole;
        } catch (Exception e) {
            throw new IllegalArgumentException("message: " + e.getMessage());
        }
    }
}
