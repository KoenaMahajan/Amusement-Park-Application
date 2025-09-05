package com.amusement.amusement_park.repository.feedback;


import com.amusement.amusement_park.Enums.IssueStatus;
import com.amusement.amusement_park.entity.feedback.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByReportedByEmail(String email);
    List<Issue> findByStatus(IssueStatus status);

}