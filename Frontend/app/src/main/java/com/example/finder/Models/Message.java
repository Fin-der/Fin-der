package com.example.finder.Models;

public class Message {
    private String id;
    private String text;
    private String name;
    // Picture profilePic
    // Picture[] sentPics
    // Date date

    Message(String id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
