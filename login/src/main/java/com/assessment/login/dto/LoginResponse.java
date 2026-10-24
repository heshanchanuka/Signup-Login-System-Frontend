package com.assessment.login.dto;


import com.assessment.login.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private User user;
    private String jwtToken;

}
