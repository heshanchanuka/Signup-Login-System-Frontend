package com.assessment.login.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String userName;
    private String userFirstName;
    private String userLastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;
}
