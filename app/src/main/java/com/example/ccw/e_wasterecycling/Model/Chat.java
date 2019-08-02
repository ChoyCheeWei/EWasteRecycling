package com.example.ccw.e_wasterecycling.Model;

import android.support.annotation.NonNull;

public class Chat implements Comparable<Chat> {

    private String Key, Sender, Receiver, Message, time, type;
    private boolean isseen;

    public Chat(String Key, String Sender, String Receiver, String Message, String time, boolean isseen,String type) {
        this.Key = Key;
        this.Sender = Sender;
        this.Receiver = Receiver;
        this.Message = Message;
        this.isseen = isseen;
        this.time = time;
        this.type=type;

    }

    public Chat() {

    }

    public String getKey() {
        return Key;
    }

    public String getSender() {
        return Sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public String getMessage() {
        return Message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int compareTo(@NonNull Chat o) {
        if (getTime() == null || o.getTime() == null)
            return 0;

        return o.getTime().compareTo(getTime());
    }
}
