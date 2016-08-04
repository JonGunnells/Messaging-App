package com.theironyard;


import java.util.ArrayList;

public class User {
    String name;
    String password;
    ArrayList<Message> messages = new ArrayList<>();


    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}