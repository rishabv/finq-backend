package com.finq.services;

import com.finq.entities.AdminUser;
import com.finq.entities.User;
import com.finq.repositories.AdminUserRepository;
import com.finq.repositories.UserRepository;
import com.finq.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try to find customer user
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return UserPrincipal.createCustomer(user);
        }

        // Then try to find admin user with role and permissions
        Optional<AdminUser> adminOpt = adminUserRepository.findByEmailWithRoleAndPermissions(email);
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            return UserPrincipal.createAdmin(admin);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
