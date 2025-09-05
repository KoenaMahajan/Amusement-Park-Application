package com.amusement.amusement_park.entity.feedback;

import jakarta.persistence.*;

@Entity
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @Column(length = 2000)
    private String response;

    // Default constructor
    public Chatbot() {
    }

    // All-args constructor
    public Chatbot(Long id, String keyword, String response) {
        this.id = id;
        this.keyword = keyword;
        this.response = response;
    }

    // Builder pattern
    public static ChatbotBuilder builder() {
        return new ChatbotBuilder();
    }

    public static class ChatbotBuilder {
        private Long id;
        private String keyword;
        private String response;

        public ChatbotBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChatbotBuilder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public ChatbotBuilder response(String response) {
            this.response = response;
            return this;
        }

        public Chatbot build() {
            return new Chatbot(id, keyword, response);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getResponse() {
        return response;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
