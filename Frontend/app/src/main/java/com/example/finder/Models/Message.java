package com.example.finder.Models;

public class Message {
    private String id;
    private String text;
    private String sender;
    private String receiver;
    private int msgType;
    // Picture profilePic
    // Picture[] sentPics
    // Date date

    public Message(String id, String text, String sender, String receiver, int msgType) {
        this.id = id;
        this.text = text;
        this.receiver = receiver;
        this.sender = sender;
        this.msgType = msgType;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return text;
    }
}
