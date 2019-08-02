package com.example.ccw.e_wasterecycling.Notification;

public class Data {

    private String user, body, title, sented;
    private int icon;

    public Data(String user, String body, String title, String sented, int icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.icon = icon;
    }

    private Data(){

    }

    public String getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public String getSented() {
        return sented;
    }

    public int getIcon() {
        return icon;
    }
}
