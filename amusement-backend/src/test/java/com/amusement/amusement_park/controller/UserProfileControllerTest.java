package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.UserProfileController;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserProfileController userProfileController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @Test
    void getProfileByEmail_whenUserExists_shouldReturnProfile() throws Exception {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        user.setVerified(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/profile/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    void getProfileByEmail_whenUserNotFound_shouldReturnNotFound() throws Exception {
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profile/{email}", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfileByEmail_whenUserExists_shouldUpdateProfile() throws Exception {
        String email = "user@example.com";
        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setName("Old Name");
        existingUser.setPhoneNumber("0000000000");

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setPhoneNumber("9999999999");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        mockMvc.perform(put("/api/profile/update/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully."));

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateProfileByEmail_whenUserNotFound_shouldReturnBadRequest() throws Exception {
        String email = "unknown@example.com";
        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setPhoneNumber("9999999999");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/profile/update/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found."));
    }
}
