package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.dto.feedback.FeedbackRequest;
import com.amusement.amusement_park.dto.feedback.FeedbackResponse;
import com.amusement.amusement_park.entity.feedback.Feedback;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.exception.ResourceNotFoundException;
import com.amusement.amusement_park.repository.feedback.FeedbackRepository;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    public List<FeedbackResponse> getAll() {

        return feedbackRepository.findAll().stream().map(this::toDto).toList();
    }

    public FeedbackResponse saveFeedback(String email, FeedbackRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRideRating(request.getRideRating());
        feedback.setCleanlinessRating(request.getCleanlinessRating());
        feedback.setStaffBehaviorRating(request.getStaffBehaviorRating());
        feedback.setFoodQualityRating(request.getFoodQualityRating());
        feedback.setComments(request.getComments());
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback saved = feedbackRepository.save(feedback);
        return toDto(saved);
    }

    public List<FeedbackResponse> getByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Feedback> feedbacks = feedbackRepository.findAllByCreatedAtBetween(startOfDay, endOfDay);
        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("No feedback found for date: " + date);
        }
        return feedbacks.stream().map(this::toDto).toList();
    }

    private FeedbackResponse toDto(Feedback feedback) {
        FeedbackResponse dto = new FeedbackResponse();
        dto.setId(feedback.getId());
        dto.setUserName(feedback.getUser().getName());
        dto.setRideRating(feedback.getRideRating());
        dto.setCleanlinessRating(feedback.getCleanlinessRating());
        dto.setStaffBehaviorRating(feedback.getStaffBehaviorRating());
        dto.setFoodQualityRating(feedback.getFoodQualityRating());
        dto.setComments(feedback.getComments());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}