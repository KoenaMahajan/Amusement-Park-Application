package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.ForgotPasswordController;
import com.amusement.amusement_park.dto.user.ForgotPasswordRequest;
import com.amusement.amusement_park.dto.user.ResetPasswordRequest;
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

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerTest {

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
    private ForgotPasswordController forgotPasswordController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(forgotPasswordController).build();
    }

    @Test
    void forgotPassword_whenEmailExists_shouldSendOtp() throws Exception {
        String email = "user@example.com";
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to your email."));

        verify(otpStore).storeOtp(eq(email), anyString());
        verify(emailService).sendEmail(eq(email), anyString(), contains("Your OTP"));
    }

    @Test
    void forgotPassword_whenEmailNotFound_shouldReturnBadRequest() throws Exception {
        String email = "unknown@example.com";
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email not registered."));
    }

    @Test
    void resetPassword_whenOtpIsValid_shouldResetPassword() throws Exception {
        String email = "user@example.com";
        String otp = "123456";
        String newPassword = "Secure1@Pass"; // ✅ Valid password

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);
        request.setOtp(otp);
        request.setNewPassword(newPassword);

        User user = new User();
        user.setEmail(email);

        when(otpStore.getOtp(email)).thenReturn(otp);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedPassword");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));

        verify(userRepository).save(user);
        verify(otpStore).clearOtp(email);
    }

    @Test
    void resetPassword_whenOtpIsInvalid_shouldReturnBadRequest() throws Exception {
        String email = "user@example.com";
        String otp = "wrongOtp";
        String newPassword = "Secure1@Pass"; // ✅ Valid password

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);
        request.setOtp(otp);
        request.setNewPassword(newPassword);

        when(otpStore.getOtp(email)).thenReturn("correctOtp");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired OTP."));
    }
}
