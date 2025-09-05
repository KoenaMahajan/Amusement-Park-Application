package com.amusement.amusement_park.controller.feedback;

import com.amusement.amusement_park.dto.feedback.FeedbackAnalytics;
import com.amusement.amusement_park.service.feedback.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/feedback/averages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackAnalytics> getAverageRatings() {
        return ResponseEntity.ok(analyticsService.getAverages());
    }

    // @GetMapping("/weekly-trend")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<Map<String, Long>> getWeeklyTrend() {
    // return ResponseEntity.ok(analyticsService.getWeeklyFeedbackTrend());
    // }
}