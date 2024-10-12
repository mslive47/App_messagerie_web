package com.inf5190.chat.messages;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void createMessage(Message message) {
        Message receiveMessage = this.messageRepository.createMessage(message);
        this.messageRepository.addMessage(receiveMessage);
    }

    public List<Message> getMessages() {
        return this.messageRepository.getMessages(null);
    }
 }
