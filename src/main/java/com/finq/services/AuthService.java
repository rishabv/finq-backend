package com.finq.services;

import com.finq.dtos.requests.AdminLoginRequest;
import com.finq.dtos.responses.LoginResponse;
import com.finq.entities.AdminUser;
import com.finq.entities.Role;
import com.finq.entities.User;
import com.finq.repositories.AdminUserRepository;
import com.finq.repositories.RoleRepository;
import com.finq.security.JwtTokenProvider;
import com.finq.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RoleRepository roleRepository;


    public LoginResponse<AdminUser, ?> login(AdminLoginRequest request) throws IllegalArgumentException {
        UserPrincipal userPrincipal = userDetailsService.loadAdminUserByUsername(request.getEmail());
        String token = jwtTokenProvider.generateTokenFromUserPrincipal(userPrincipal);
        AdminUser adminUser = adminUserRepository.getReferenceById(userPrincipal.getId());
        Optional<Role> role = roleRepository.findByIdWithPermissions(adminUser.getRole().getId());
        if(!role.isPresent()) {
            throw new IllegalArgumentException("");
        }
        List<String> permissions = role.get().getPermissions().stream().map(i->i.getPermissionCode()).toList();
        return new LoginResponse<AdminUser, String>(adminUser, token, permissions);
    }
}
