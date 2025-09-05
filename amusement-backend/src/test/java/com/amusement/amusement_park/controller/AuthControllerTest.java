package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.AuthController;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import com.amusement.amusement_park.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnToken() throws Exception {
        String email = "user@example.com";
        String password = "securePass";
        String encodedPassword = "hashedPass";
        String role = "USER";
        String token = "mocked-jwt-token";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email, role)).thenReturn(token);

        Map<String, String> requestBody = Map.of("email", email, "password", password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.message").value("Login successful. Welcome USER!"));
    }

    @Test
    void login_whenEmailNotFound_shouldReturnBadRequest() throws Exception {
        String email = "unknown@example.com";
        String password = "anyPass";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Map<String, String> requestBody = Map.of("email", email, "password", password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password."));
    }

    @Test
    void login_whenPasswordIsIncorrect_shouldReturnBadRequest() throws Exception {
        String email = "user@example.com";
        String password = "wrongPass";
        String encodedPassword = "hashedPass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole("USER");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        Map<String, String> requestBody = Map.of("email", email, "password", password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password."));
    }
}
