package com.mycode.entity;

import com.mycode.constant.Constant;

public class User {

    private String index;
    private String username;
    private String password;
    private String name;
    private String status = Constant.STATUS_READY;

    public User() {
    }
    public User(String index, String username, String password, String name) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.name = name;
    }
    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
    public User(String index, String username, String password, String name, String status) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.name = name;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return index + ":" + name + "(" + status + ")";
    }

    public String getAllMessage() {
        return "User{" +
                "index='" + index + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
