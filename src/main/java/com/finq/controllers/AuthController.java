package com.finq.controllers;

import com.finq.dtos.requests.AdminLoginRequest;
import com.finq.dtos.responses.BaseApiResponse;
import com.finq.dtos.responses.LoginResponse;
import com.finq.entities.AdminUser;
import com.finq.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        LoginResponse<AdminUser, ?> response = authService.login(request);
        return ResponseEntity.ok(new BaseApiResponse<>(response));
    }
}
