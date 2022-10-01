package com.rj.chatrj.model;



import java.util.List;

public class MessageList extends StandardResponse {

   private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
