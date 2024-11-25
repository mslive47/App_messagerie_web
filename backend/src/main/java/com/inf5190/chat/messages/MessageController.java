package com.inf5190.chat.messages;

import com.inf5190.chat.ChatApplication;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/auth/chat";
    public static final String MESSAGE_PATH_WITH_ID = "/auth/chat/{id}";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

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
     * @param newMessageRequest le message à creer
     * @return receiveMessage le message créé
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@RequestHeader("Authorization") String authHeader, @RequestBody NewMessageRequest newMessageRequest) {
        try {

            if (authHeader == null || !authHeader.startsWith("Bearer")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing or invalid Authorization header");
            }

            String jwtToken = authHeader.substring(6);
            SessionData userData = this.sessionManager.getSession(jwtToken);
            return this.messageService.createMessage(userData.username(), newMessageRequest);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.warn("Erreur inattendue lors de la création du message.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inattendue lors de la création du message.");
        }
    }

    /**
     * Cette methode permet d'obtenir la liste des messages
     * @param fromId l'id du message
     * @return la liste des messages
     * */
    @GetMapping(MESSAGES_PATH)
    public @ResponseBody List<Message> getMessages(@RequestParam(required = false) String fromId) {
        try {
            return this.messageService.getMessages(fromId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.warn("Erreur inattendue lors de la récupération des messages.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inattendue lors de la récupération des messages.");
        }
    }

    /**
     * Cette methode permet d'obtenir un message de la liste des messages
     * @param id l'id du message
     * @return le message
     * */
    @GetMapping(MESSAGE_PATH_WITH_ID)
    public @ResponseBody List<Message> getMessagesById(@PathVariable String id) {
        try {
            return this.messageService.getMessages(id);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.warn("Erreur inattendue lors de la récupération d'un message.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inattendue lors de la récupération d'un message.");
        }
    }
}
