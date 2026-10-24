package com.assessment.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    private String userName;
    private String userFirstName;
    private String userLastName;
    private String userPassword;
}
