package com.amusement.amusement_park.dto.feedback;

public class FeedbackAnalytics {
    private double averageRideRating;
    private double averageCleanlinessRating;
    private double averageStaffBehaviorRating;
    private double averageFoodQualityRating;
    private long totalFeedbackCount;

    // Default constructor
    public FeedbackAnalytics() {
    }

    // All-args constructor
    public FeedbackAnalytics(double averageRideRating, double averageCleanlinessRating,
            double averageStaffBehaviorRating, double averageFoodQualityRating, long totalFeedbackCount) {
        this.averageRideRating = averageRideRating;
        this.averageCleanlinessRating = averageCleanlinessRating;
        this.averageStaffBehaviorRating = averageStaffBehaviorRating;
        this.averageFoodQualityRating = averageFoodQualityRating;
        this.totalFeedbackCount = totalFeedbackCount;
    }

    // Builder pattern
    public static FeedbackAnalyticsBuilder builder() {
        return new FeedbackAnalyticsBuilder();
    }

    public static class FeedbackAnalyticsBuilder {
        private double averageRideRating;
        private double averageCleanlinessRating;
        private double averageStaffBehaviorRating;
        private double averageFoodQualityRating;
        private long totalFeedbackCount;

        public FeedbackAnalyticsBuilder averageRideRating(double averageRideRating) {
            this.averageRideRating = averageRideRating;
            return this;
        }

        public FeedbackAnalyticsBuilder averageCleanlinessRating(double averageCleanlinessRating) {
            this.averageCleanlinessRating = averageCleanlinessRating;
            return this;
        }

        public FeedbackAnalyticsBuilder averageStaffBehaviorRating(double averageStaffBehaviorRating) {
            this.averageStaffBehaviorRating = averageStaffBehaviorRating;
            return this;
        }

        public FeedbackAnalyticsBuilder averageFoodQualityRating(double averageFoodQualityRating) {
            this.averageFoodQualityRating = averageFoodQualityRating;
            return this;
        }

        public FeedbackAnalyticsBuilder totalFeedbackCount(long totalFeedbackCount) {
            this.totalFeedbackCount = totalFeedbackCount;
            return this;
        }

        public FeedbackAnalytics build() {
            return new FeedbackAnalytics(averageRideRating, averageCleanlinessRating, averageStaffBehaviorRating,
                    averageFoodQualityRating, totalFeedbackCount);
        }
    }

    // Getters
    public double getAverageRideRating() {
        return averageRideRating;
    }

    public double getAverageCleanlinessRating() {
        return averageCleanlinessRating;
    }

    public double getAverageStaffBehaviorRating() {
        return averageStaffBehaviorRating;
    }

    public double getAverageFoodQualityRating() {
        return averageFoodQualityRating;
    }

    public long getTotalFeedbackCount() {
        return totalFeedbackCount;
    }

    // Setters
    public void setAverageRideRating(double averageRideRating) {
        this.averageRideRating = averageRideRating;
    }

    public void setAverageCleanlinessRating(double averageCleanlinessRating) {
        this.averageCleanlinessRating = averageCleanlinessRating;
    }

    public void setAverageStaffBehaviorRating(double averageStaffBehaviorRating) {
        this.averageStaffBehaviorRating = averageStaffBehaviorRating;
    }

    public void setAverageFoodQualityRating(double averageFoodQualityRating) {
        this.averageFoodQualityRating = averageFoodQualityRating;
    }

    public void setTotalFeedbackCount(long totalFeedbackCount) {
        this.totalFeedbackCount = totalFeedbackCount;
    }
}
