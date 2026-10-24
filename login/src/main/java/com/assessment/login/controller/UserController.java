package com.assessment.login.controller;

import com.assessment.login.dto.SignupRequest;
import com.assessment.login.entity.User;
import com.assessment.login.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping({"/user/register-new-user", "/register-new-user"})
    public User registerNewUser(@RequestBody SignupRequest signUpRequest) {
        return userService.registerNewUser(signUpRequest);
    }

    @GetMapping({"/user/profile", "/profile"})
    public User getUserProfile(Authentication authentication) {
        String currentUsername = authentication != null ? authentication.getName() : "Anonymous";
        return userService.getUserByUsername(currentUsername);
    }

    @PostConstruct
    public void initRoleAndUser(){
        userService.initRoleAndUser();
    }

    @GetMapping({"for-admin"})
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin(){
        return "This URL is only accessible For Admin";
    }

    @GetMapping({"for-user"})
    @PreAuthorize("hasRole('User')")
    public String forUser(){
        return "This URL is only accessible For User";
    }
}
