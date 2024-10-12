package com.inf5190.chat.messages;

import com.inf5190.chat.messages.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;

import java.util.List;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/auth/chat";

    private WebSocketManager webSocketManager;
    private MessageService messageService;

    public MessageController(MessageService messageService,
                             WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.messageService = messageService;
    }

    // À faire...
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(MESSAGES_PATH)
    public void createMessage(@RequestBody Message message) {
        this.messageService.createMessage(message);
    }

    @GetMapping(MESSAGES_PATH)
    public @ResponseBody List<Message> getMessages() {
        return this.messageService.getMessages();
    }
}
