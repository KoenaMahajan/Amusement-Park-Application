package com.amusement.amusement_park.controller.user;

import com.amusement.amusement_park.entity.user.MembershipPlan;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.repository.user.MembershipPlanRepository;
import com.amusement.amusement_park.repository.user.UserMembershipRepository;
import com.amusement.amusement_park.repository.user.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-memberships")
public class UserMembershipController {

    private final UserRepository userRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserMembershipRepository userMembershipRepository;

    private static final String USER_NOT_FOUND = "User not found.";
    private static final String ACTIVE = "ACTIVE";
    private static final String CANCELLED = "CANCELLED";

    public UserMembershipController(UserRepository userRepository, MembershipPlanRepository membershipPlanRepository,
            UserMembershipRepository userMembershipRepository) {
        this.userRepository = userRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.userMembershipRepository = userMembershipRepository;
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<String> subscribeToPlan(@PathVariable Long planId, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND);
        }

        User user = userOpt.get();

        List<UserMembership> activePlans = userMembershipRepository.findByUserAndStatus(user, ACTIVE);
        if (!activePlans.isEmpty()) {
            return ResponseEntity.badRequest().body("User already has an active membership.");
        }

        Optional<MembershipPlan> planOpt = membershipPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Plan not found.");
        }

        MembershipPlan plan = planOpt.get();

        UserMembership newMembership = new UserMembership();
        newMembership.setUser(user);
        newMembership.setPlan(plan);
        newMembership.setStartDate(LocalDate.now());

        if (plan.getDurationInDays() > 0) {
            newMembership.setEndDate(LocalDate.now().plusDays(plan.getDurationInDays()));
        }

        newMembership.setStatus(ACTIVE);

        userMembershipRepository.save(newMembership);

        return ResponseEntity.ok("Subscribed to plan successfully.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserMembership>> viewMyMembership(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();

        List<UserMembership> memberships = userMembershipRepository.findByUser(user);

        // Optionally, update status for expired ones
        for (UserMembership membership : memberships) {
            if (membership.getEndDate() != null &&
                    membership.getEndDate().isBefore(LocalDate.now()) &&
                    !"EXPIRED".equals(membership.getStatus())) {
                membership.setStatus("EXPIRED");
                userMembershipRepository.save(membership);
            }
        }

        // Always return the list, even if empty
        return ResponseEntity.ok(memberships);
    }

    @PutMapping("/cancel")
    public ResponseEntity<String> cancelMembership(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND);
        }

        User user = userOpt.get();
        List<UserMembership> activePlans = userMembershipRepository.findByUserAndStatus(user, ACTIVE);

        if (activePlans.isEmpty()) {
            return ResponseEntity.badRequest().body("No active memberships found.");
        }

        for (UserMembership membership : activePlans) {
            membership.setStatus(CANCELLED);
            membership.setEndDate(LocalDate.now());
        }

        userMembershipRepository.saveAll(activePlans);
        return ResponseEntity.ok("Membership cancelled.");
    }
}