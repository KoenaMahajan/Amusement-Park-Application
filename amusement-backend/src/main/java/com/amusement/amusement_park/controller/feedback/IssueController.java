package com.amusement.amusement_park.controller.feedback;

import com.amusement.amusement_park.Enums.IssueStatus;
import com.amusement.amusement_park.dto.feedback.IssueRequest;
import com.amusement.amusement_park.dto.feedback.IssueResponse;
import com.amusement.amusement_park.service.feedback.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<IssueResponse> reportIssue(Authentication authentication,
            @Valid @RequestBody IssueRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(issueService.reportIssue(email, request));
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<IssueResponse>> getUserIssues(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(issueService.getUserIssues(email));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> updateStatus(@PathVariable Long id,
            @RequestParam("status") IssueStatus status) {
        return ResponseEntity.ok(issueService.updateIssueStatus(id, status));
    }

    @GetMapping("/status") // all according to status can be viewed
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IssueResponse>> getIssuesByStatus(@RequestParam("status") IssueStatus status) {
        return ResponseEntity.ok(issueService.getIssuesByStatus(status));
    }
}
