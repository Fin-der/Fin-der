package com.example.finder.Models;

public class Message {
    private String id;
    private String text;
    private String sender;
    private String senderName;
    private int msgType;
    // Picture profilePic
    // Picture[] sentPics
    // Date date

    public Message(String id, String text, String sender, String senderName, int msgType) {
        this.id = id;
        this.text = text;
        this.senderName = senderName;
        this.sender = sender;
        this.msgType = msgType;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return text;
    }

    public int getMsgType() {
        return msgType;
    }
}
