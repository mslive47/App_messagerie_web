package com.inf5190.chat.messages;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Service utilisé par le MessageController.
 */
@Service
public class MessageService {

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageService(MessageRepository messageRepository, WebSocketManager webSocketManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    /**
     * Cette methode permet de créer un message
     * @param message le message à creer
     * @return receiveMessage le message créé
     * */
    public Message createMessage(Message message) {
        Message receiveMessage = this.messageRepository.createMessage(message);
        this.messageRepository.addMessage(receiveMessage);
        this.webSocketManager.notifySessions();
        return receiveMessage;
    }

    /**
     * Cette methode de retourner la liste de message
     * @param fromId, l'id du message
     * @return la liste de messages
     * */
    public List<Message> getMessages(@RequestParam(required = false) Long fromId) {
        return this.messageRepository.getMessages(fromId);
    }
 }
