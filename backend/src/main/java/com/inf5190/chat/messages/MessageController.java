package com.inf5190.chat.messages;

import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;
import com.inf5190.chat.messages.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/auth/chat";
    public static final String MESSAGE_PATH_WITH_ID = "/auth/chat/{id}";

    private WebSocketManager webSocketManager;
    private MessageService messageService;
    private SessionManager sessionManager;

    public MessageController(WebSocketManager webSocketManager,
                             MessageService messageService, SessionManager sessionManager) {
        this.webSocketManager = webSocketManager;
        this.messageService = messageService;
        this.sessionManager = sessionManager;
    }

    // À faire...
    /**
     * Cette methode permet de créer un message
     * @param message le message à creer
     * @return receiveMessage le message créé
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@RequestHeader("Authorization") String authHeader, @RequestBody Message message)
            throws ExecutionException, InterruptedException {
        // Check if the header contains a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing or invalid Authorization header");
        }

        // Extract the JWT and get the authenticated username
        String jwtToken = authHeader.substring(6); // Remove "Bearer" prefix
        SessionData userData = this.sessionManager.getSession(jwtToken);
        return this.messageService.createMessage(userData.username(), message);
    }

    /**
     * Cette methode permet d'obtenir la liste des messages
     * @param fromId l'id du message
     * @return la liste des messages
     * */
    @GetMapping(MESSAGES_PATH)
    public @ResponseBody List<Message> getMessages(@RequestParam(required = false) String fromId)
            throws ExecutionException, InterruptedException {
        return this.messageService.getMessages(fromId);
    }

    /**
     * Cette methode permet d'obtenir un message de la liste des messages
     * @param id l'id du message
     * @return le message
     * */
    @GetMapping(MESSAGE_PATH_WITH_ID)
    public @ResponseBody List<Message> getMessagesById(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        return this.messageService.getMessages(id);
    }
}
