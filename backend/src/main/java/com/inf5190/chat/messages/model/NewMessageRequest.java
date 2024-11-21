package com.inf5190.chat.messages.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Représente un message recu du frontend.
 */
public class NewMessageRequest {
    private String text;
    private String username;
    @JsonProperty("imageData")
    private ChatImageData imageData;

    public NewMessageRequest() {
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public ChatImageData getImageData() {
        return imageData;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageData(ChatImageData imageData) {
        this.imageData = imageData;
    }
}