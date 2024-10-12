package com.inf5190.chat.messages.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.inf5190.chat.messages.model.Message;

/**
 * Classe qui gère la persistence des messages.
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    private final List<Message> messages = new ArrayList<Message>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Message> getMessages(Long fromId) {
        // À faire...
        return this.messages;
    }

    public Message createMessage(Message message) {
        // À faire...
        long id = idGenerator.getAndIncrement();
        Message newMessage;
        newMessage = new Message(id, message.username(), message.timestamp(), message.text());
        return newMessage;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

}
