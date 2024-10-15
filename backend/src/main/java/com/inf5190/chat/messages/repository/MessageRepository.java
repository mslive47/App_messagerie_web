package com.inf5190.chat.messages.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import com.inf5190.chat.messages.model.Message;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Classe qui gère la persistence des messages.
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    private final List<Message> messages = new ArrayList<Message>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Message> getMessages(@RequestParam(required = false) Long fromId) {
        // À faire...
        if (fromId != null) {
            return this.messages.stream().filter(message -> message.id() >= fromId).collect(Collectors.toList());
        } else  {
            return this.messages;
        }
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
