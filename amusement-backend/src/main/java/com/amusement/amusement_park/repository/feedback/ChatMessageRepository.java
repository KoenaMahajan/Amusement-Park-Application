package com.amusement.amusement_park.repository.feedback;





import com.amusement.amusement_park.entity.feedback.ChatMessage;
import com.amusement.amusement_park.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByUser(User user);
}