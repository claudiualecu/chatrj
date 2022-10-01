package com.rj.chatrj.controller;
import com.rj.chatrj.Util;
import com.rj.chatrj.model.Conversation;
import com.rj.chatrj.model.Message;
import com.rj.chatrj.model.StandardResponse;
import com.rj.chatrj.model.User;
import com.rj.chatrj.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;


@Controller
public class ChatController {


    @Autowired
    ConversationService conversationService;

    @MessageMapping("/message")
    @SendTo("/topic/message")
    public Message message(Message message, Authentication authentication) throws Exception {
        StringBuilder response = new StringBuilder();
        StringBuilder fullName = new StringBuilder();
        Integer userId = null;
        try {
            userId = Util.getUserIdFromAuth(authentication);
        }catch (Exception e){
            throw new Exception("No user ID!");
        }
        fullName.append(((User)((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUserInfo().get("first_name")).append(" ").append(((User)((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUserInfo().get("last_name"));
        response.append(fullName.toString()).append(" : ").append(message.getContent());
        String token =((User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getToken();
        return conversationService.addMessageToConversation(userId, message);
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public Message typing(Authentication authentication) throws Exception {
        StringBuilder response = new StringBuilder();
        StringBuilder fullName = new StringBuilder();
        fullName.append(((User)((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUserInfo().get("first_name")).append(" ").append(((User)((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUserInfo().get("last_name"));
        response.append(fullName.toString()).append(" is typing! ");
        return new Message( HtmlUtils.htmlEscape(response.toString()));
    }

    @MessageMapping("/readMessages")
    @SendTo("/topic/readMessages")
    public StandardResponse readMessages(Conversation conversation, Authentication authentication) throws Exception {
        Integer userId = null;
        try {
            userId = Util.getUserIdFromAuth(authentication);
        }catch (Exception e){
            throw new Exception("No user ID!");
        }

        String token =((User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getToken();
        return conversationService.readMessages(userId, conversation.getId(), token);
    }

}
