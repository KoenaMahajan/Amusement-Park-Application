package com.amusement.amusement_park.repository.feedback;



import com.amusement.amusement_park.Enums.LostFoundStatus;
import com.amusement.amusement_park.entity.feedback.LostAndFound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LostAndFoundRepository extends JpaRepository<LostAndFound, Long> {
    List<LostAndFound> findByReportedByEmail(String email);
    List<LostAndFound> findByStatus(LostFoundStatus status);
    List<LostAndFound> findAllByReportedAtBetween(LocalDateTime start, LocalDateTime end);

}
