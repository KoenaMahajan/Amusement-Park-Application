package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amusement.amusement_park.dto.user.UserProfile;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserProfile> getProfileByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email).map(user -> {
            UserProfile userProfile = new UserProfile();
            userProfile.setEmail(user.getEmail());
            userProfile.setName(user.getName());
            userProfile.setPhoneNumber(user.getPhoneNumber());
            userProfile.setRole(user.getRole());
            userProfile.setVerified(user.isVerified());
            return ResponseEntity.ok(userProfile);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<String> updateProfileByEmail(
            @PathVariable String email,
            @Valid @RequestBody User updatedUser) {

        return userRepository.findByEmail(email).map(user -> {
            user.setName(updatedUser.getName() != null ? updatedUser.getName() : user.getName());
            user.setPhoneNumber(
                    updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : user.getPhoneNumber());
            userRepository.save(user);
            return ResponseEntity.ok("Profile updated successfully.");
        }).orElse(ResponseEntity.badRequest().body("User not found."));
    }
}
