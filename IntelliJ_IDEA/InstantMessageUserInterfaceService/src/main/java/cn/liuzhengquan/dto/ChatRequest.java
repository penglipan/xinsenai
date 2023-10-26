package cn.liuzhengquan.dto;

import cn.liuzhengquan.util.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {

    private String model;
    private List<Message> messages;

    public ChatRequest(String model, String userInput) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", userInput));
    }

    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
