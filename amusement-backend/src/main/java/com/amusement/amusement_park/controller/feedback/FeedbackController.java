package com.amusement.amusement_park.controller.feedback;


import com.amusement.amusement_park.dto.feedback.FeedbackRequest;
import com.amusement.amusement_park.dto.feedback.FeedbackResponse;
import com.amusement.amusement_park.service.feedback.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FeedbackResponse> save(Authentication authentication, @Valid @RequestBody FeedbackRequest request
    ) {
        String email = authentication.getName();
        FeedbackResponse response = feedbackService.saveFeedback(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getAllByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(feedbackService.getByDate(date));
    }
}
