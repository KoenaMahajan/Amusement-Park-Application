package com.amusement.amusement_park.exception;



public class ChatbotKeywordNotFoundException extends RuntimeException {
  public ChatbotKeywordNotFoundException(String message) {
    super("No matching keyword found for message: " + message);
  }
  public ChatbotKeywordNotFoundException(Long id) {
    super("Chatbot keyword not found with ID: " + id);
  }



}


