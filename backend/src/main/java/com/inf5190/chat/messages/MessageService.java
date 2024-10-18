package com.inf5190.chat.messages;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageService(MessageRepository messageRepository, WebSocketManager webSocketManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    public Message createMessage(Message message) {
        Message receiveMessage = this.messageRepository.createMessage(message);
        this.messageRepository.addMessage(receiveMessage);
        this.webSocketManager.notifySessions();
        return receiveMessage;
    }

    public List<Message> getMessages(@RequestParam(required = false) Long fromId) {
        return this.messageRepository.getMessages(fromId);
    }
 }
