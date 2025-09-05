package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.entity.user.MembershipPlan;

import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.repository.user.MembershipPlanRepository;
import com.amusement.amusement_park.repository.user.UserMembershipRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MembershipPlanController {

    private final MembershipPlanRepository membershipPlanRepository;

    private final UserMembershipRepository userMembershipRepository;

    public MembershipPlanController(MembershipPlanRepository membershipPlanRepository,
            UserMembershipRepository userMembershipRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.userMembershipRepository = userMembershipRepository;
    }

    @PostMapping("/admin/membership-plans")
    public ResponseEntity<String> addPlan(@RequestBody MembershipPlan plan) {
        membershipPlanRepository.save(plan);
        return ResponseEntity.ok("Membership plan added successfully.");
    }

    @GetMapping("/membership-plans")
    public List<MembershipPlan> getAllPlans() {
        return membershipPlanRepository.findAll();
    }

    @GetMapping("/admin/user-memberships")
    public List<UserMembership> getAllUserMemberships() {
        return userMembershipRepository.findAll();
    }

    @GetMapping("/admin/membership-plans/{planId}/users-with-count")
    public ResponseEntity<?> getUsersAndCountByMembershipPlan(@PathVariable Long planId) {
        Optional<MembershipPlan> planOpt = membershipPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plan not found.");
        }

        MembershipPlan plan = planOpt.get();
        List<UserMembership> userMemberships = userMembershipRepository.findByPlan(plan);
        int count = userMemberships.size();

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("memberships", userMemberships);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/membership-plans/{id}")
    public ResponseEntity<String> updatePlan(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<MembershipPlan> optionalPlan = membershipPlanRepository.findById(id);
        if (optionalPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plan not found.");
        }

        MembershipPlan plan = optionalPlan.get();

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> plan.setName((String) value);
                case "description" -> plan.setDescription((String) value);
                case "price" -> plan.setPrice(Double.parseDouble(value.toString()));
                case "durationInDays" -> plan.setDurationInDays((Integer) value);
            }
        });

        membershipPlanRepository.save(plan);
        return ResponseEntity.ok("Membership plan updated successfully.");
    }

    @DeleteMapping("/admin/membership-plans/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Long id) {
        if (!membershipPlanRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plan not found.");
        }

        membershipPlanRepository.deleteById(id);
        return ResponseEntity.ok("Plan deleted successfully.");
    }
}
