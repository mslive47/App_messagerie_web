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
    public static final String MESSAGES_PATH = "/auth/chat";
    public static final String MESSAGE_PATH_WITH_ID = "/auth/chat/{id}";


    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageController(MessageRepository messageRepository, WebSocketManager webSocketManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    // À faire...
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@RequestBody Message message) {
        Message postedMessage = this.messageService.createMessage(message);
        return postedMessage;
    }

    @GetMapping(MESSAGES_PATH)
    public @ResponseBody List<Message> getMessages(@RequestParam(required = false) Long fromId) {
        return this.messageService.getMessages(fromId);
    }

    @GetMapping(MESSAGE_PATH_WITH_ID)
    public @ResponseBody List<Message> getMessagesById(@PathVariable Long id){
        return this.messageService.getMessages(id);
    }

}
