package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.dto.feedback.FeedbackAnalytics;
import com.amusement.amusement_park.repository.feedback.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {
    private final FeedbackRepository feedbackRepository;

    public AnalyticsService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public FeedbackAnalytics getAverages() {
        List<Object[]> results = feedbackRepository.findAverageRatings();
        Object[] result = results.isEmpty() ? new Object[4] : results.get(0);

        return FeedbackAnalytics.builder()
                .averageRideRating(result[0] != null ? ((Number) result[0]).doubleValue() : 0.0)
                .averageCleanlinessRating(result[1] != null ? ((Number) result[1]).doubleValue() : 0.0)
                .averageStaffBehaviorRating(result[2] != null ? ((Number) result[2]).doubleValue() : 0.0)
                .averageFoodQualityRating(result[3] != null ? ((Number) result[3]).doubleValue() : 0.0)
                .totalFeedbackCount(feedbackRepository.count())
                .build();
    }

    // public Map<String, Long> getWeeklyFeedbackTrend() {
    // LocalDateTime start = LocalDate.now().minusDays(6).atStartOfDay();
    // List<Object[]> results = feedbackRepository.getFeedbackCountByDay(start);
    //
    // Map<String, Long> map = new HashMap<>();
    // for (Object[] row : results) {
    // map.put(row[0].toString(), (Long) row[1]);
    // }
    // return map;
    // }
}
