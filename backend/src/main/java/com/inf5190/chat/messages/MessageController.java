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
    public static final String MESSAGE_PATH_WITH_ID = "/auth/chat/{id}";

    private WebSocketManager webSocketManager;
    private MessageService messageService;

    public MessageController(MessageService messageService,
                             WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.messageService = messageService;
    }

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
    public @ResponseBody List<Message> getMessagesById(@PathVariable Long id){
        return this.messageService.getMessages(id);
    }
}
