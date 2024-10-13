package com.inf5190.chat.messages;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageService(MessageRepository messageRepository, WebSocketManager webSocketManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    public void createMessage(Message message) {
        Message receiveMessage = this.messageRepository.createMessage(message);
        this.messageRepository.addMessage(receiveMessage);
        this.webSocketManager.notifySessions();
    }

    public List<Message> getMessages() {
        return this.messageRepository.getMessages(null);
    }
 }
