package com.amusement.amusement_park.repository.feedback;

import com.amusement.amusement_park.entity.feedback.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface chatbotRepository extends JpaRepository<Chatbot, Long> {
    Optional<Chatbot> findByKeywordIgnoreCase(String keyword);

    @Query("SELECT c FROM Chatbot c WHERE LOWER(:input) LIKE CONCAT('%', LOWER(c.keyword), '%')")
    List<Chatbot> findByKeywordInMessage(@Param("input") String input);
}