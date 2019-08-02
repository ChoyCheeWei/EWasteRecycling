package com.example.ccw.e_wasterecycling.Model;

import android.support.annotation.NonNull;

public class Contacts implements Comparable<Contacts> {

    private String Uid, adminId, adminImage, adminName, admin_Email, email, imageUrl, username, time;

    public Contacts(String uid, String adminId, String adminImage, String adminName,
                    String admin_Email, String email, String imageUrl, String username, String time) {
        this.Uid = uid;
        this.adminId = adminId;
        this.adminImage = adminImage;
        this.adminName = adminName;
        this.admin_Email = admin_Email;
        this.email = email;
        this.imageUrl = imageUrl;
        this.username = username;
        this.time = time;
    }

    public Contacts(){

    }

    public String getUid() {
        return Uid;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminImage() {
        return adminImage;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdmin_Email() {
        return admin_Email;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int compareTo(@NonNull Contacts o) {
        if (getTime() == null || o.getTime() == null)
            return 0;

        return o.getTime().compareTo(getTime());
    }
}
