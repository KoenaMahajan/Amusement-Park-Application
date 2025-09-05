package com.amusement.amusement_park.entity.feedback;

import com.amusement.amusement_park.entity.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String botReply;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP) // temporal type=timestamp means both date and time is stored
    private Date createdAt;

    // Default constructor
    public ChatMessage() {
    }

    // All-args constructor
    public ChatMessage(Long id, User user, String userMessage, String botReply, Date createdAt) {
        this.id = id;
        this.user = user;
        this.userMessage = userMessage;
        this.botReply = botReply;
        this.createdAt = createdAt;
    }

    // Builder pattern
    public static ChatMessageBuilder builder() {
        return new ChatMessageBuilder();
    }

    public static class ChatMessageBuilder {
        private Long id;
        private User user;
        private String userMessage;
        private String botReply;
        private Date createdAt;

        public ChatMessageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChatMessageBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ChatMessageBuilder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public ChatMessageBuilder botReply(String botReply) {
            this.botReply = botReply;
            return this;
        }

        public ChatMessageBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(id, user, userMessage, botReply, createdAt);
        }
    }

    @PrePersist // it will excetute tjust before saving(time) this entity
    protected void onCreate() {
        this.createdAt = new Date();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

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
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

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