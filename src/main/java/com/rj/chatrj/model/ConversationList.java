package com.rj.chatrj.model;



import java.util.List;

public class ConversationList extends StandardResponse {

   private List<Conversation> conversations;

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
