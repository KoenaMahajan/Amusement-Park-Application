package com.amusement.amusement_park.repository.user;

import com.amusement.amusement_park.entity.user.UserMembership;
import com.amusement.amusement_park.entity.user.MembershipPlan;
import com.amusement.amusement_park.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    List<UserMembership> findByUser(User user);

    List<UserMembership> findByUserAndStatus(User user, String status);

    List<UserMembership> findByPlan(MembershipPlan plan);

    List<UserMembership> findByStatus(String status);

}