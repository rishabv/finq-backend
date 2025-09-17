package com.finq.security;

import com.finq.entities.AdminUser;
import com.finq.entities.Permission;
import com.finq.entities.Role;
import com.finq.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private UUID id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String customerId;
    private String employeeId;
    private String userType;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Role role;
    private Collection<GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.customerId = user.getCustomerId();
        this.userType = "CUSTOMER";
        this.accountNonExpired = true;
        this.accountNonLocked = !user.isAccountLocked();
        this.credentialsNonExpired = true;
        this.enabled = user.isActive();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }

    public UserPrincipal(AdminUser adminUser) {
        this.id = adminUser.getId();
        this.email = adminUser.getEmail();
        this.password = adminUser.getPasswordHash();
        this.firstName = adminUser.getFirstName();
        this.lastName = adminUser.getLastName();
        this.employeeId = adminUser.getEmployeeId();
        this.userType = "ADMIN";
        this.accountNonExpired = true;
        this.accountNonLocked = !adminUser.isActive();
        this.credentialsNonExpired = true;
        this.enabled = adminUser.isActive();
        this.role = adminUser.getRole();

        // Set authorities based on role and permissions
        Set<Permission> permissions = adminUser.getRole().getPermissions();
        this.authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("PERM_" + permission.getPermissionCode().toUpperCase()))
                .collect(Collectors.toSet());

        this.authorities.add(new SimpleGrantedAuthority("ROLE_" + adminUser.getRole().getRoleCode().toUpperCase()));

    }

    public static UserPrincipal createCustomer(User user) {
        return new UserPrincipal(user);
    }

    public static UserPrincipal createAdmin(AdminUser adminUser) {
        return new UserPrincipal(adminUser);
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public String getUsername() {
        return firstName;
    }
}
