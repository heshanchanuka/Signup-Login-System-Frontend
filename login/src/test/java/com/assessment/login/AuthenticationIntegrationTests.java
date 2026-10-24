package com.assessment.login;

import com.assessment.login.dto.LoginRequest;
import com.assessment.login.dto.SignupRequest;
import com.assessment.login.repo.UserRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    private final String testUser = "qa_tester_user";

    @BeforeEach
    @AfterEach
    void cleanUp() {
        if (userRepo.existsById(testUser)) {
            userRepo.deleteById(testUser);
        }
    }

    @Test
    void testCompleteAuthenticationWorkflow() throws Exception {
        // 1. Successful Signup
        SignupRequest signupRequest = new SignupRequest(
                testUser,
                "Kamal",
                "Perera",
                "Secret@12345"
        );

        MvcResult signupResult = mockMvc.perform(post("/user/register-new-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode signupNode = objectMapper.readTree(signupResult.getResponse().getContentAsString());
        assertThat(signupNode.get("userName").asText()).isEqualTo(testUser);
        assertThat(signupNode.get("userFirstName").asText()).isEqualTo("Kamal");
        assertThat(signupNode.has("userPassword")).isFalse();

        // 2. Duplicate Signup should return 409 Conflict
        mockMvc.perform(post("/user/register-new-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("User with username '" + testUser + "' already exists"));

        // 3. Signup with empty username should return 400 Bad Request
        SignupRequest invalidSignup = new SignupRequest("", "John", "Doe", "pwd123");
        mockMvc.perform(post("/user/register-new-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSignup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));

        // 4. Successful Login via primary endpoint (/authentication)
        LoginRequest loginRequest = new LoginRequest(testUser, "Secret@12345");
        MvcResult loginResult = mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        assertThat(loginNode.has("jwtToken")).isTrue();
        String jwtToken = loginNode.get("jwtToken").asText();
        assertThat(jwtToken).isNotEmpty();
        assertThat(loginNode.get("user").get("userName").asText()).isEqualTo(testUser);
        assertThat(loginNode.get("user").has("userPassword")).isFalse();

        // 5. Successful Login via alias (/login)
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());

        // 6. Login with WRONG password should return 401 Unauthorized
        LoginRequest wrongPass = new LoginRequest(testUser, "WrongPassword");
        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPass)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

        // 7. Login with NON-EXISTENT user should return 401 Unauthorized
        LoginRequest nonExistentUser = new LoginRequest("unknown_user_999", "AnyPass");
        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        // 8. Access Protected /user/profile WITH valid JWT
        MvcResult profileResult = mockMvc.perform(get("/user/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode profileNode = objectMapper.readTree(profileResult.getResponse().getContentAsString());
        assertThat(profileNode.get("userName").asText()).isEqualTo(testUser);
        assertThat(profileNode.get("userFirstName").asText()).isEqualTo("Kamal");
        assertThat(profileNode.has("userPassword")).isFalse();

        // 9. Access Protected /user/me alias WITH valid JWT
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(testUser));

        // 10. Access Protected /user/profile WITHOUT token -> 401 Unauthorized
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized());

        // 11. Access Protected /user/profile WITH invalid / malformed token -> 401 Unauthorized
        mockMvc.perform(get("/user/profile")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }
}
