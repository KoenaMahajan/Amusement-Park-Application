package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.dto.feedback.ChatMessageDTO;
import com.amusement.amusement_park.entity.feedback.ChatMessage;
import com.amusement.amusement_park.entity.feedback.Chatbot;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.exception.UserNotFoundException;
import com.amusement.amusement_park.repository.feedback.ChatMessageRepository;
import com.amusement.amusement_park.repository.feedback.chatbotRepository;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;
    private final chatbotRepository chatbotRepo;

    public ChatService(ChatMessageRepository chatRepo, UserRepository userRepo, chatbotRepository chatbotRepo) {
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
        this.chatbotRepo = chatbotRepo;
    }

    public String generateReply(String message) {
        String input = message.toLowerCase();

        // Exact keyword match by word
        for (String word : input.split("\\s+")) {
            Optional<Chatbot> chatbot = chatbotRepo.findByKeywordIgnoreCase(word);
            if (chatbot.isPresent())
                return chatbot.get().getResponse();
        }

        // if Keyword containment match via DB query
        List<Chatbot> matches = chatbotRepo.findByKeywordInMessage(input);
        if (!matches.isEmpty())
            return matches.get(0).getResponse();

        return "Sorry, I didn't understand that. You can ask about ticket prices, timings, rides, food, bookings, and more.";

    }

    public void saveChat(String email, String userMessage, String botReply) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        ChatMessage chat = ChatMessage.builder()
                .user(user)
                .userMessage(userMessage)
                .botReply(botReply)
                .build();

        chatRepo.save(chat);
    }

    public List<ChatMessageDTO> getChatHistory(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return chatRepo.findAllByUser(user).stream()
                .map(chat -> new ChatMessageDTO(chat.getUserMessage(), chat.getBotReply(), chat.getCreatedAt()))
                .toList();
    }
}