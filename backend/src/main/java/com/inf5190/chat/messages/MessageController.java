package com.inf5190.chat.messages;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

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
    private final MessageService messageService;
    private WebSocketManager webSocketManager;

    public MessageController(WebSocketManager WebSocketManager , MessageService messageService) {
        this.webSocketManager = webSocketManager;
        this.messageService = messageService;
    }

    // À faire...
   
    // À faire...
    /**
     * Cette methode permet de créer un message
     * @param message le message à creer
     * @return receiveMessage le message créé
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@RequestBody Message message) {
        Message postedMessage = this.messageService.createMessage(message);
        return postedMessage;
    }

    /**
     * Cette methode permet d'obtenir la liste des messages
     * @param fromId l'id du message
     * @return la liste des messages
     * */
    @GetMapping(MESSAGES_PATH)
    public @ResponseBody List<Message> getMessages(@RequestParam(required = false) Long fromId) {
        return this.messageService.getMessages(fromId);
    }

    /**
     * Cette methode permet d'obtenir un message de la liste des messages
     * @param id l'id du message
     * @return le message
     * */
    @GetMapping(MESSAGE_PATH_WITH_ID)
    public @ResponseBody List<Message> getMessagesById(@PathVariable Long id) {
        return this.messageService.getMessages(id);
    }
}
