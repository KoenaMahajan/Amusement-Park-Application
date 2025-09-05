package com.amusement.amusement_park.dto.feedback;

public class ChatMessageRequest {
    private String message;

    // Default constructor
    public ChatMessageRequest() {
    }

    // All-args constructor
    public ChatMessageRequest(String message) {
        this.message = message;
    }

    // Builder pattern
    public static ChatMessageRequestBuilder builder() {
        return new ChatMessageRequestBuilder();
    }

    public static class ChatMessageRequestBuilder {
        private String message;

        public ChatMessageRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ChatMessageRequest build() {
            return new ChatMessageRequest(message);
        }
    }

    // Getters
    public String getMessage() {
        return message;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }
}
