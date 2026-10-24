package com.assessment.login.service;

import com.assessment.login.dto.SignupRequest;
import com.assessment.login.entity.User;
import com.assessment.login.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    public User registerNewUser(SignupRequest signUpRequest) {
        if (signUpRequest == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (signUpRequest.getUserName() == null || signUpRequest.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (signUpRequest.getUserPassword() == null || signUpRequest.getUserPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String username = signUpRequest.getUserName().trim();
        if (userRepo.existsById(username)) {
            throw new IllegalStateException("User with username '" + username + "' already exists");
        }

        User user = new User();
        user.setUserName(username);
        user.setUserPassword(getEncodedPassword(signUpRequest.getUserPassword()));
        user.setUserFirstName(signUpRequest.getUserFirstName());
        user.setUserLastName(signUpRequest.getUserLastName());

        return userRepo.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepo.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
