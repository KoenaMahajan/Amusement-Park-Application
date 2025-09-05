package com.amusement.amusement_park.controller.feedback;

import com.amusement.amusement_park.entity.feedback.Chatbot;
import com.amusement.amusement_park.service.feedback.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addKeyword(@RequestBody Chatbot chatbot) {
        chatbotService.addKeyword(chatbot);
        return ResponseEntity.ok("Keyword added successfully!");
    }

    @PostMapping("/add-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addMultipleKeywords(@RequestBody List<Chatbot> chatbotList) {
        chatbotService.addAllKeywords(chatbotList);
        return ResponseEntity.ok("All keywords added successfully!");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Chatbot> getAllKeywords() {
        return chatbotService.getAllKeywords();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteKeyword(@PathVariable Long id) {
        chatbotService.deleteKeyword(id);
        return ResponseEntity.ok("Deleted successfully.");
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateKeyword(@PathVariable Long id, @RequestBody Chatbot updatedData) {
        chatbotService.updateKeyword(id, updatedData);
        return ResponseEntity.ok("Updated successfully.");
    }
}