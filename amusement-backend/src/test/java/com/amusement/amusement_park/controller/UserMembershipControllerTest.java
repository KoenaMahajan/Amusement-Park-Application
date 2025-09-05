package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.UserMembershipController;
import com.amusement.amusement_park.entity.user.MembershipPlan;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.repository.user.MembershipPlanRepository;
import com.amusement.amusement_park.repository.user.UserMembershipRepository;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserMembershipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MembershipPlanRepository membershipPlanRepository;
    @Mock
    private UserMembershipRepository userMembershipRepository;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserMembershipController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void subscribeToPlan_success() throws Exception {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        MembershipPlan plan = new MembershipPlan();
        plan.setId(1L);

        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(Collections.emptyList());
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(plan));

        mockMvc.perform(post("/api/user-memberships/subscribe/1")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscribed to plan successfully."));
    }

    @Test
    void subscribeToPlan_userNotFound() throws Exception {
        when(authentication.getName()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/user-memberships/subscribe/1")
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found."));
    }

    @Test
    void subscribeToPlan_alreadySubscribed() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        UserMembership active = new UserMembership();
        active.setStatus("ACTIVE");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(List.of(active));

        mockMvc.perform(post("/api/user-memberships/subscribe/1")
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already has an active membership."));
    }

    @Test
    void subscribeToPlan_planNotFound() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(Collections.emptyList());
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/user-memberships/subscribe/1")
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Plan not found."));
    }

    @Test
    void viewMyMembership_shouldReturnMembershipsAndUpdateExpired() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        UserMembership expired = new UserMembership();
        expired.setEndDate(LocalDate.now().minusDays(1));
        expired.setStatus("ACTIVE");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUser(user)).thenReturn(List.of(expired));

        mockMvc.perform(get("/api/user-memberships/my")
                .principal(authentication))
                .andExpect(status().isOk());
        verify(userMembershipRepository).save(expired);
    }

    @Test
    void cancelMembership_success() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        UserMembership active = new UserMembership();
        active.setStatus("ACTIVE");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(List.of(active));

        mockMvc.perform(put("/api/user-memberships/cancel")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Membership cancelled."));
        verify(userMembershipRepository).saveAll(List.of(active));
    }

    @Test
    void cancelMembership_noActiveMemberships() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMembershipRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/api/user-memberships/cancel")
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No active memberships found."));
    }
}
