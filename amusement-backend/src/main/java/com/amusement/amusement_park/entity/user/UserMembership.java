package com.amusement.amusement_park.entity.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class UserMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private MembershipPlan plan;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, CANCELLED, EXPIRED

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserMembership that = (UserMembership) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(plan, that.plan) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, plan, startDate, endDate, status);
    }
}