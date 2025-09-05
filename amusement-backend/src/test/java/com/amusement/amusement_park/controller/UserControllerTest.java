package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.UserController;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import com.amusement.amusement_park.service.user.EmailService;
import com.amusement.amusement_park.service.user.OtpStore;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpStore otpStore;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void registerUser_whenSuccessful_shouldReturnOk() throws Exception {
        Map<String, String> requestBody = Map.of(
                "email", "test@example.com",
                "password", "Password123!",
                "role", "USER");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered successfully. OTP sent to console (valid for 5 min)."));

        verify(userRepository).save(any(User.class));
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void verifyOtp_whenOtpIsValid_shouldReturnOk() throws Exception {
        String email = "test@example.com";
        String otp = "123456";
        Map<String, String> requestBody = Map.of("email", email, "otp", otp);
        User user = new User();
        user.setVerified(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpStore.getOtp(email)).thenReturn(otp);

        mockMvc.perform(post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP verified. Account activated."));

        verify(userRepository).save(any(User.class));
        verify(otpStore).clearOtp(email);
    }

    @Test
    void resendOtp_whenUserExistsAndNotVerified_shouldReturnOk() throws Exception {
        String email = "test@example.com";
        Map<String, String> requestBody = Map.of("email", email);
        User user = new User();
        user.setVerified(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/resend-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP resent to your email (console for now)."));
    }
}
