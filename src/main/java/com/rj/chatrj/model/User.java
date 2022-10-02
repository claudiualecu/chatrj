package com.rj.chatrj.model;


import java.util.HashMap;
import java.util.Map;

public class User {

    private Integer id;
    private String username;
    private String password;
    private String token;
    private Integer unitId;
    private Map<String, Object> userInfo;

    public User(String username, String password,Integer unitId, Map<String, Object> userInfo, String token){
        this.password=password;
        this.username=username;
        this.userInfo = userInfo;
        this.unitId = unitId;
        this.token = token;
    }

    public User(String username, String password, Map<String, Object> userInfo){
        this.password=password;
        this.username=username;
        this.userInfo = userInfo;
    }
    public User(String username, String password, Map<String, Object> userInfo, String token){
        this.password=password;
        this.username=username;
        this.userInfo = userInfo;
        this.token = token;
    }
    public User(){
        this.password = null;
        this.username = null;
        this.userInfo = new HashMap<>();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
