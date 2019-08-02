package com.example.ccw.e_wasterecycling.Model;

public class User {

    private String email;
    private String username;
    private String password;
    private String phone;
    private String confirmpassword;
    private String address;
    private String imageUrl;
    private String Uid, adminId, adminName, adminImage, status;


    public User(String email, String username, String password, String phone, String confirmpassword, String address,
                String imageUrl, String Uid, String adminId, String adminName, String adminImage, String status) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.confirmpassword = confirmpassword;
        this.address = address;
        this.imageUrl = imageUrl;
        this.Uid = Uid;
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminImage = adminImage;
        this.status = status;

    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUid() {
        return Uid;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminImage() {
        return adminImage;
    }

    public String getStatus() {
        return status;
    }

}
