package com.assessment.login.controller;

import com.assessment.login.dto.SignupRequest;
import com.assessment.login.entity.User;
import com.assessment.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/user", ""})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping({"/register-new-user", "/signup", "/register"})
    public ResponseEntity<User> registerNewUser(@RequestBody SignupRequest signUpRequest) {
        User createdUser = userService.registerNewUser(signUpRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping({"/profile", "/me"})
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        String currentUsername = authentication != null ? authentication.getName() : "Anonymous";
        User user = userService.getUserByUsername(currentUsername);
        return ResponseEntity.ok(user);
    }
}
