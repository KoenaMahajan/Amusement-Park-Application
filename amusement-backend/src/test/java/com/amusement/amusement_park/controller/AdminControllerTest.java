package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.AdminController;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

    @Test
    void updateUserRolePhoneAndName_shouldUpdateFields() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/admin/update/{id}", userId)
                .param("role", "admin")
                .param("phoneNumber", "1234567890")
                .param("name", "Updated Name"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully (role/phone/name)."));

        verify(userRepository).save(user);
    }

    @Test
    void updateUserRolePhoneAndName_whenUserNotFound_shouldReturnBadRequest() throws Exception {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/admin/update/{id}", userId)
                .param("role", "admin"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found with id: " + userId));
    }

    @Test
    void deleteUser_whenUserExists_shouldDeleteSuccessfully() throws Exception {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/delete/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully with id: " + userId));

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_shouldReturnBadRequest() throws Exception {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/delete/{id}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found with id: " + userId));
    }
}
