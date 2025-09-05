package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.user.MembershipPlanController;
import com.amusement.amusement_park.entity.user.MembershipPlan;
import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.repository.user.MembershipPlanRepository;
import com.amusement.amusement_park.repository.user.UserMembershipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MembershipPlanControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MembershipPlanRepository membershipPlanRepository;
    @Mock
    private UserMembershipRepository userMembershipRepository;

    @InjectMocks
    private MembershipPlanController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addPlan_shouldSaveAndReturnSuccess() throws Exception {
        MembershipPlan plan = new MembershipPlan();
        plan.setName("Gold Plan");
        plan.setPrice(999.0);

        mockMvc.perform(post("/api/admin/membership-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(plan)))
                .andExpect(status().isOk())
                .andExpect(content().string("Membership plan added successfully."));

        verify(membershipPlanRepository).save(plan);
    }

    @Test
    void getAllPlans_shouldReturnList() throws Exception {
        MembershipPlan plan = new MembershipPlan();
        plan.setName("Silver Plan");

        when(membershipPlanRepository.findAll()).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/membership-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Silver Plan"));
    }

    @Test
    void getAllUserMemberships_shouldReturnList() throws Exception {
        UserMembership membership = new UserMembership();
        membership.setStatus("ACTIVE");

        when(userMembershipRepository.findAll()).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/admin/user-memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void getUsersAndCountByMembershipPlan_shouldReturnData() throws Exception {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(1L);
        UserMembership membership = new UserMembership();
        membership.setPlan(plan);

        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(userMembershipRepository.findByPlan(plan)).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/admin/membership-plans/1/users-with-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.memberships[0].plan.id").value(1));
    }

    @Test
    void getUsersAndCountByMembershipPlan_planNotFound_shouldReturn404() throws Exception {
        when(membershipPlanRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/membership-plans/99/users-with-count"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Plan not found."));
    }

    @Test
    void updatePlan_shouldApplyUpdates() throws Exception {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(1L);
        plan.setName("Old Name");
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("price", 499.0);

        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(plan));

        mockMvc.perform(patch("/api/admin/membership-plans/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().string("Membership plan updated successfully."));

        verify(membershipPlanRepository).save(plan);
    }

    @Test
    void updatePlan_planNotFound_shouldReturn404() throws Exception {
        Map<String, Object> updates = Map.of("name", "New Name");

        when(membershipPlanRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/admin/membership-plans/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Plan not found."));
    }

    @Test
    void deletePlan_shouldDeleteSuccessfully() throws Exception {
        when(membershipPlanRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/membership-plans/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Plan deleted successfully."));

        verify(membershipPlanRepository).deleteById(1L);
    }

    @Test
    void deletePlan_planNotFound_shouldReturn404() throws Exception {
        when(membershipPlanRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/membership-plans/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Plan not found."));
    }
}
