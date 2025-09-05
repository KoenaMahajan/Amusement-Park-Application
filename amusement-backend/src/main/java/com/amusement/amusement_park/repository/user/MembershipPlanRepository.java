package com.amusement.amusement_park.repository.user;

import com.amusement.amusement_park.entity.user.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
}