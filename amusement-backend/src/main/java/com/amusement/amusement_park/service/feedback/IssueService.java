package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.Enums.IssueStatus;
import com.amusement.amusement_park.dto.feedback.IssueRequest;
import com.amusement.amusement_park.dto.feedback.IssueResponse;
import com.amusement.amusement_park.entity.feedback.Issue;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.exception.ResourceNotFoundException;
import com.amusement.amusement_park.repository.feedback.IssueRepository;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public IssueResponse reportIssue(String userEmail, IssueRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Issue issue = Issue.builder()
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(IssueStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .reportedBy(user)
                .build();

        return toDto(issueRepository.save(issue));
    }

    public List<IssueResponse> getUserIssues(String email) {
        return issueRepository.findByReportedByEmail(email)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public IssueResponse updateIssueStatus(Long id, IssueStatus status) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));

        issue.setStatus(status);
        if (status == IssueStatus.RESOLVED || status == IssueStatus.CLOSED) {
            issue.setResolvedAt(LocalDateTime.now());
        }

        return toDto(issueRepository.save(issue));
    }

    public List<IssueResponse> getIssuesByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    private IssueResponse toDto(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .subject(issue.getSubject())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .reportedBy(issue.getReportedBy().getName())
                .createdAt(issue.getCreatedAt())
                .resolvedAt(issue.getResolvedAt())
                .build();
    }

}