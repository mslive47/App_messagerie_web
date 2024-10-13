package com.inf5190.chat.messages;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List; 
import com.inf5190.chat.messages.model.Message;

import org.springframework.web.bind.annotation.RestController;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;

import org.springframework.web.bind.annotation.RequestParam;



/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/messages";


    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageController(MessageRepository messageRepository,
            WebSocketManager webSocketManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    // À faire...

     /**
     * Récupère tous les messages.
     *
     * @return la liste des messages.
     */
    @GetMapping(MESSAGES_PATH)
    public ResponseEntity<List<Message>> getMessages(@RequestParam(required = false) Long fromId){
        List<Message> messages = messageRepository.getMessages(fromId);
        return ResponseEntity.ok(messages);
    }

/**
     * Publie un nouveau message.
     *
     * @param message le message à publier.
     * @return le message publié.
     */
    @PostMapping(MESSAGES_PATH)
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Message createdMessage = messageRepository.createMessage(message);
        webSocketManager.notifySessions(); // Appel d'une notification générale via WebSocketManager
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
}
