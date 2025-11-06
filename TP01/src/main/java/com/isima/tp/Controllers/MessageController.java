package com.isima.tp.Controllers;

import com.isima.tp.business.MessageService;
import com.isima.tp.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Envoyer un message
     */
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Map<String, Object> request) {
        Long expediteurId = Long.parseLong(request.get("expediteurId").toString());
        Long destinataireId = Long.parseLong(request.get("destinataireId").toString());
        String contenu = request.get("contenu").toString();
        
        Message sentMessage = messageService.sendMessage(expediteurId, destinataireId, contenu);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
    }

    /**
     * Récupérer tous les messages entre deux utilisateurs
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(
            @RequestParam Long expediteurId,
            @RequestParam Long destinataireId) {
        List<Message> messages = messageService.getConversation(expediteurId, destinataireId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Récupérer les messages reçus par un utilisateur
     */
    @GetMapping("/received/{destinataireId}")
    public ResponseEntity<List<Message>> getReceivedMessages(@PathVariable Long destinataireId) {
        List<Message> messages = messageService.getMessagesRecus(destinataireId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Récupérer les messages envoyés par un utilisateur
     */
    @GetMapping("/sent/{expediteurId}")
    public ResponseEntity<List<Message>> getSentMessages(@PathVariable Long expediteurId) {
        List<Message> messages = messageService.getMessagesEnvoyes(expediteurId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marquer un message comme lu
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
}
