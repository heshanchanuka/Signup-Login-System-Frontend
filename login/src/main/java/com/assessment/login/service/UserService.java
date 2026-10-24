package com.assessment.login.service;

import com.assessment.login.dto.SignupRequest;
import com.assessment.login.entity.Role;
import com.assessment.login.entity.User;
import com.assessment.login.repo.RoleRepo;
import com.assessment.login.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    public User registerNewUser(SignupRequest signUpRequest) {
        if (!userRepo.existsById(signUpRequest.getUserName())) {
            User user = new User();
            user.setUserName(signUpRequest.getUserName());
            user.setUserPassword(getEncodedPassword(signUpRequest.getUserPassword()));
            user.setUserFirstName(signUpRequest.getUserFirstName());
            user.setUserLastName(signUpRequest.getUserLastName());

            Set<Role> adminRoles = new HashSet<>();

            if (signUpRequest.getRole() != null && signUpRequest.getRole().equalsIgnoreCase("USER")) {
                Role role = new Role();
                role.setRoleName(signUpRequest.getRole());
                adminRoles.add(role);
            } else if (signUpRequest.getRole() == null || signUpRequest.getRole().trim().isEmpty()) {
                Role role = new Role();
                role.setRoleName("USER");
                adminRoles.add(role);
            } else {
                throw new RuntimeException("No Roles Like TGHis");
            }

            user.setRole(adminRoles);
            return userRepo.save(user);
        }
        throw new RuntimeException("User already exists");
    }

    public User getUserByUsername(String username) {
        return userRepo.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public void initRoleAndUser(){
        Role adminRole = new Role();
        Role userRole = new Role();

        if (!roleRepo.existsById("ADMIN")) {
            adminRole.setRoleName("ADMIN");
            adminRole.setRoleDescription("This is an Admin Role");
            roleRepo.save(adminRole);
        }

        if (!roleRepo.existsById("USER")) {
            userRole.setRoleName("USER");
            userRole.setRoleDescription("This is an User Role");
            roleRepo.save(userRole);
        }

        if(!userRepo.existsById("admin123")){
            User user = new User();
            user.setUserName("admin123");
            user.setUserPassword(getEncodedPassword("admin123"));
            user.setUserLastName("Heshan");
            user.setUserFirstName("Chanuka");

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            user.setRole(adminRoles);
            userRepo.save(user);
        }

        if(!userRepo.existsById("user123")){
            User user = new User();
            user.setUserName("user123");
            user.setUserPassword(getEncodedPassword("user123"));
            user.setUserLastName("Chamith");
            user.setUserFirstName("Kavishan");

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            user.setRole(userRoles);
            userRepo.save(user);
        }
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
