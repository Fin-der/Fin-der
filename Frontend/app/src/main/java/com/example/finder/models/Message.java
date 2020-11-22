package com.example.finder.models;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Message {
    private String id;
    private String text;
    private String sender;
    private String senderName;
    private int msgType;
    private String postAt;
    // Picture profilePic
    // Picture[] sentPics
    // Date date

    public Message(String id, String text, String sender, String senderName, int msgType, String postAt) {
        this.id = id;
        this.text = text;
        this.senderName = senderName;
        this.sender = sender;
        this.msgType = msgType;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        parse.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, hh:mm a");
        date.setTimeZone(TimeZone.getDefault());
        System.out.println(TimeZone.getDefault());
        try {
            this.postAt = date.format(parse.parse(postAt));
        } catch (ParseException e) {
            e.printStackTrace();
            this.postAt = "sike";
        }
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getPostAt() {
        return postAt;
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
