package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.dto.user.ForgotPasswordRequest;
import com.amusement.amusement_park.dto.user.ResetPasswordRequest;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import com.amusement.amusement_park.service.user.OtpGenerator;
import com.amusement.amusement_park.service.user.OtpStore;

import jakarta.validation.Valid;

import com.amusement.amusement_park.service.user.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final OtpStore otpStore;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    public ForgotPasswordController(UserRepository userRepository, OtpStore otpStore,
            BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpStore = otpStore;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered.");
        }

        String otp = OtpGenerator.generateOtp();
        otpStore.storeOtp(request.getEmail(), otp);

        logger.info("Forgot password OTP for {} is: {}", request.getEmail(), otp);

        emailService.sendEmail(
                request.getEmail(),
                "Password Reset OTP - Amusement Park",
                "Your OTP to reset your password is: " + otp + "\nThis OTP is valid for 5 minutes.");

        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String storedOtp = otpStore.getOtp(request.getEmail());

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpStore.clearOtp(request.getEmail());

        logger.info("Password successfully reset for {}", request.getEmail());

        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
