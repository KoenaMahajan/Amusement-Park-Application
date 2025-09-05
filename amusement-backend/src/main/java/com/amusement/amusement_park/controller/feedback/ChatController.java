package com.amusement.amusement_park.controller.feedback;

import com.amusement.amusement_park.dto.feedback.ChatMessageDTO;
import com.amusement.amusement_park.dto.feedback.ChatMessageRequest;
import com.amusement.amusement_park.service.feedback.ChatService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatMessageRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        String userMessage = request.getMessage();
        String reply = chatService.generateReply(userMessage);
        chatService.saveChat(email, userMessage, reply);

        return ResponseEntity.ok(Map.of("reply", reply));
    }

    @GetMapping("/chathistory")
    @PreAuthorize("hasRole('USER')")

    public ResponseEntity<List<ChatMessageDTO>> getHistory(Authentication authentication) {
        String email = authentication.getName();
        List<ChatMessageDTO> messages = chatService.getChatHistory(email);
        return ResponseEntity.ok(messages);
    }
}