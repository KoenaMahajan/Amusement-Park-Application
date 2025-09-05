package com.amusement.amusement_park.dto.feedback;

import java.util.Date;

public class ChatMessageDTO {
    private String userMessage;
    private String botReply;
    private Date createdAt;

    // Default constructor
    public ChatMessageDTO() {
    }

    // All-args constructor
    public ChatMessageDTO(String userMessage, String botReply, Date createdAt) {
        this.userMessage = userMessage;
        this.botReply = botReply;
        this.createdAt = createdAt;
    }

    // Getters
    public String getUserMessage() {
        return userMessage;
    }

    public String getBotReply() {
        return botReply;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setBotReply(String botReply) {
        this.botReply = botReply;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
