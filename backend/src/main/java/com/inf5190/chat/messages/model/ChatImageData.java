package com.inf5190.chat.messages.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Représente un image.
 */
public class ChatImageData {
    @JsonProperty("data")
    private String data;
    @JsonProperty("type")
    private String type;

    public ChatImageData() {
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}