package com.rj.chatrj.model;


import java.util.List;

public class Conversation extends StandardResponse{

    private Integer id;
    private String name;
    private Integer noUnreadMessages;
    private List<Integer> userIdList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNoUnreadMessages() {
        return noUnreadMessages;
    }

    public void setNoUnreadMessages(Integer noUnreadMessages) {
        this.noUnreadMessages = noUnreadMessages;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}
