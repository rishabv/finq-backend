package com.finq.services;

import com.finq.entities.AdminUser;
import com.finq.entities.User;
import com.finq.repositories.AdminUserRepository;
import com.finq.repositories.UserRepository;
import com.finq.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPrincipal userPrincipal;

    public UserPrincipal loadAdminUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AdminUser> foundUser = adminUserRepository.findFirstByEmail(email);
        if(foundUser.isEmpty()){
            throw new UsernameNotFoundException("User not found", null);
        }
        return UserPrincipal.createAdmin(foundUser.get());
    }

    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> foundUser = userRepository.findFirstByEmail(email);
        if(foundUser.isEmpty()){
            throw new UsernameNotFoundException("User not found", null);
        }
        return UserPrincipal.createCustomer(foundUser.get());
    }

}
