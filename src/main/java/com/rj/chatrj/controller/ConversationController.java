package com.rj.chatrj.controller;



import com.rj.chatrj.SysLogger;
import com.rj.chatrj.Util;
import com.rj.chatrj.model.Conversation;
import com.rj.chatrj.model.ConversationList;
import com.rj.chatrj.model.MessageList;
import com.rj.chatrj.model.StandardResult;
import com.rj.chatrj.service.ConversationService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/conversation")
public class ConversationController {
    @Autowired
    private ConversationService conversationService;

    @GetMapping("/getConversationList")
    public ConversationList getConversationList(Authentication authentication){
        ConversationList response = new ConversationList();
        response.setResult(StandardResult.OK.toString());
        try {
            Integer userId = null;
            try {
                userId = Util.getUserIdFromAuth(authentication);
            }catch (Exception e){
                throw new Exception("No user ID!");
            }
            response.setConversations(conversationService.getConversations(userId));
        } catch (Throwable th){
            Pair<String,String> mst = Util.getMessageAndStackTrace(th);
            SysLogger.logErr(mst.getKey());
            SysLogger.logErr(mst.getValue());
            response.setResult(StandardResult.ERROR.toString());
            response.setInfo(th.getMessage());
        }

        return response;
    }

    @GetMapping("/getNotificationNumber")
    public Conversation getNotificationNumber(Authentication authentication){
        Conversation response = new Conversation();
        response.setResult(StandardResult.OK.toString());
        try {
            Integer userId = null;
            try {
                userId = Util.getUserIdFromAuth(authentication);
            }catch (Exception e){
                throw new Exception("No user ID!");
            }
            response = conversationService.getNotificationNumber(userId);
        } catch (Throwable th){
            Pair<String,String> mst = Util.getMessageAndStackTrace(th);
            SysLogger.logErr(mst.getKey());
            SysLogger.logErr(mst.getValue());
            response.setResult(StandardResult.ERROR.toString());
            response.setInfo(th.getMessage());
        }

        return response;
    }

    @GetMapping("/getMessagesList/{conversationId}")
    public MessageList getMessagesList(@PathVariable Integer conversationId, Authentication authentication){
        MessageList response = new MessageList();
        response.setResult(StandardResult.OK.toString());
        try {
            Integer userId = null;
            try {
                userId = Util.getUserIdFromAuth(authentication);
            }catch (Exception e){
                throw new Exception("No user ID!");
            }
            response.setMessages(conversationService.getMessages(userId, conversationId));
        } catch (Throwable th){
            Pair<String,String> mst = Util.getMessageAndStackTrace(th);
            SysLogger.logErr(mst.getKey());
            SysLogger.logErr(mst.getValue());
            response.setResult(StandardResult.ERROR.toString());
            response.setInfo(th.getMessage());
        }

        return response;
    }

    @GetMapping("/addNewMessage/{userIdTo}")
    public Conversation addNewMessage(@PathVariable Integer userIdTo, Authentication authentication){
        Conversation response = new Conversation();
        response.setResult(StandardResult.OK.toString());
        try {
            Integer userId = null;
            try {
                userId = Util.getUserIdFromAuth(authentication);
            }catch (Exception e){
                throw new Exception("No user ID!");
            }
            response = conversationService.addNewMessage(userId, userIdTo);
        } catch (Throwable th){
            Pair<String,String> mst = Util.getMessageAndStackTrace(th);
            SysLogger.logErr(mst.getKey());
            SysLogger.logErr(mst.getValue());
            response.setResult(StandardResult.ERROR.toString());
            response.setInfo(th.getMessage());
        }

        return response;
    }

    @PostMapping("/addConversation")
    public Conversation addConversation(@RequestBody Conversation conversation, Authentication authentication){
        Conversation response = new Conversation();
        response.setResult(StandardResult.OK.toString());
        try {
            Integer userId = null;
            try {
                userId = Util.getUserIdFromAuth(authentication);
            }catch (Exception e){
                throw new Exception("No user ID!");
            }
            response = conversationService.addConversation(userId, conversation);
        } catch (Throwable th){
            Pair<String,String> mst = Util.getMessageAndStackTrace(th);
            SysLogger.logErr(mst.getKey());
            SysLogger.logErr(mst.getValue());
            response.setResult(StandardResult.ERROR.toString());
            response.setInfo(th.getMessage());
        }

        return response;
    }

}
