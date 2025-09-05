package com.amusement.amusement_park.service.user;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.repository.user.UserMembershipRepository;

@Service
public class MembershipStatusUpdater {

    private final UserMembershipRepository userMembershipRepository;

    public MembershipStatusUpdater(UserMembershipRepository userMembershipRepository) {
        this.userMembershipRepository = userMembershipRepository;
    }

     @Scheduled(cron = "0 5 0 * * ?")
    public void updateExpiredMemberships() {
        LocalDate today = LocalDate.now();
        List<UserMembership> activeMemberships = userMembershipRepository.findByStatus("ACTIVE");

        for (UserMembership membership : activeMemberships) {
            if (membership.getEndDate() != null && membership.getEndDate().isBefore(today)) {
                membership.setStatus("EXPIRED");
                userMembershipRepository.save(membership);
            }
        }
    }
}