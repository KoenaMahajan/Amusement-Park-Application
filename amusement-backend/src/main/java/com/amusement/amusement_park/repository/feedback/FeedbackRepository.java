package com.amusement.amusement_park.repository.feedback;


import com.amusement.amusement_park.entity.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    List<Feedback> findAllByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);



    @Query("SELECT AVG(f.rideRating), AVG(f.cleanlinessRating), AVG(f.staffBehaviorRating), AVG(f.foodQualityRating) FROM Feedback f")
    List<Object[]> findAverageRatings();


}


