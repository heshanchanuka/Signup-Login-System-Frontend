package com.assessment.login.controller;

import com.assessment.login.dto.LoginRequest;
import com.assessment.login.dto.LoginResponse;
import com.assessment.login.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private JwtService jwtService;

    @PostMapping({"/authentication", "/authenticate", "/login"})
    public ResponseEntity<LoginResponse> createJwtTokenAndLogin(@RequestBody LoginRequest loginRequest) throws Exception {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        LoginResponse response = jwtService.createJwtToken(loginRequest);
        return ResponseEntity.ok(response);
    }
}
