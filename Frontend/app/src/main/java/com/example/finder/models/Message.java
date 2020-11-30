package com.example.finder.models;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Model class used to store information of sent/received information
 *
 */
public class Message {
    private final String id;
    private final String text;
    private final String sender;
    private final String senderName;
    private final int msgType;
    private String postAt;

    public Message(String id, String text, String sender, String senderName, int msgType, String postAt) {
        this.id = id;
        this.text = text;
        this.senderName = senderName;
        this.sender = sender;
        this.msgType = msgType;
        /*
         * Messages retrieved from the backend will contain a timestamp formatted in the
         * backend's timezone so it is necessary to convert timezones first for the user
         */
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parse =
                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        parse.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, hh:mm a");
        date.setTimeZone(TimeZone.getDefault());
        System.out.println(TimeZone.getDefault());
        try {
            this.postAt = date.format(parse.parse(postAt));
        } catch (ParseException e) {
            e.printStackTrace();
            this.postAt = "ERROR: COULD NOT CHANGE TIMEZONE";
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Message))
            return false;

        Message compare = (Message) obj;

        if (!compare.getId().equals(this.id))
            return false;
        if (!compare.getMessage().equals(this.text))
            return false;
        if (!compare.getSenderName().equals(this.senderName))
            return false;

        return compare.getSender().equals(this.getSender());
    }
}
