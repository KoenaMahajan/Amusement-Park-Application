package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUserRolePhoneAndName(

            @PathVariable Long id,

            @RequestParam(required = false) String role,

            @RequestParam(required = false) String phoneNumber,

            @RequestParam(required = false) String name) {

        return userRepository.findById(id).map(user -> {
            if (role != null && !role.trim().isEmpty()) {
                user.setRole(role.toUpperCase());
            }
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                user.setPhoneNumber(phoneNumber);
            }
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name);
            }
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully (role/phone/name).");
        }).orElse(ResponseEntity.badRequest().body("User not found with id: " + id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully with id: " + id);
    }
}
