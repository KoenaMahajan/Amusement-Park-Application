package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.entity.feedback.Chatbot;
import com.amusement.amusement_park.exception.ResourceNotFoundException;
import com.amusement.amusement_park.repository.feedback.chatbotRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ChatbotService {

    private final chatbotRepository chatbotRepo;

    public ChatbotService(chatbotRepository chatbotRepo) {
        this.chatbotRepo = chatbotRepo;
    }

    // adding one data at a time
    public void addKeyword(Chatbot chatbot) {
        validateChatbot(chatbot);
        chatbotRepo.save(chatbot);
    }

    public void addAllKeywords(List<Chatbot> chatbotList) {
        if (chatbotList == null || chatbotList.isEmpty()) {
            throw new IllegalArgumentException("Chatbot list cannot be null or empty.");
        }
        chatbotList.forEach(this::validateChatbot);
        chatbotRepo.saveAll(chatbotList);
    }

    private void validateChatbot(Chatbot chatbot) {
        if (chatbot == null) {
            throw new IllegalArgumentException("Chatbot object cannot be null.");
        }
        if (!StringUtils.hasText(chatbot.getKeyword())) {
            throw new IllegalArgumentException("Keyword cannot be empty.");
        }
        if (!StringUtils.hasText(chatbot.getResponse())) {
            throw new IllegalArgumentException("Response cannot be empty.");
        }
    }

    public List<Chatbot> getAllKeywords() {
        List<Chatbot> list = chatbotRepo.findAll();
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No chatbot keywords found.");
        }
        return list;
    }

    public void deleteKeyword(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided for deletion.");
        }

        if (!chatbotRepo.existsById(id)) {
            throw new ResourceNotFoundException("Chatbot with ID " + id + " not found.");
        }

        chatbotRepo.deleteById(id);
    }

    public Chatbot updateKeyword(Long id, Chatbot updatedChatbot) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided for update.");
        }
        validateChatbot(updatedChatbot);

        Chatbot existingBot = chatbotRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chatbot with ID " + id + " not found."));
        existingBot.setKeyword(updatedChatbot.getKeyword());
        existingBot.setResponse(updatedChatbot.getResponse());

        return chatbotRepo.save(existingBot);
    }

}
