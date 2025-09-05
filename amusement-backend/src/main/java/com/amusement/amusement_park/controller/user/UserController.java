package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import com.amusement.amusement_park.service.user.OtpGenerator;
import com.amusement.amusement_park.service.user.OtpStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import com.amusement.amusement_park.service.user.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final OtpStore otpStore;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final EmailService emailService;

    public UserController(UserRepository userRepository, OtpStore otpStore, BCryptPasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.otpStore = otpStore;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> body) {// @RequestBody Map<String,
                                                                                       // String> body
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            return ResponseEntity.badRequest().body(
                    "Password must contain at least 8 characters, including an uppercase letter, a lowercase letter, a number, and a special character.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setVerified(false);

        userRepository.save(user);

        String otp = OtpGenerator.generateOtp();
        otpStore.storeOtp(email, otp);
        logger.info("OTP for {} is: {}", email, otp);
        emailService.sendEmail(email, "Your OTP for Amusement Park", "Your OTP is: " + otp);
        // replace with email service later

        return ResponseEntity.ok("Registered successfully. OTP sent to console (valid for 5 min).");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide an email.");
        }

        if (otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide the OTP.");
        }

        return userRepository.findByEmail(email)
                .map(user -> {
                    String storedOtp = otpStore.getOtp(email);
                    if (storedOtp == null) {
                        return ResponseEntity.badRequest().body("OTP expired or not found.");
                    }
                    if (!storedOtp.equals(otp)) {
                        return ResponseEntity.badRequest().body("Invalid OTP.");
                    }
                    user.setVerified(true);
                    userRepository.save(user);
                    otpStore.clearOtp(email);
                    return ResponseEntity.ok("OTP verified. Account activated.");
                })
                .orElse(ResponseEntity.badRequest().body("User not found."));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already verified.");
        }

        String otp = OtpGenerator.generateOtp();
        otpStore.storeOtp(email, otp);

        logger.info("Resent OTP for {} is: {}", email, otp);
        emailService.sendEmail(email, "Your OTP for Amusement Park", "Your OTP is: " + otp);

        return ResponseEntity.ok("OTP resent to your email (console for now).");
    }
}
