package com.example.ccw.e_wasterecycling.Model;

public class Product {


    private String email, Uid, address, condition, imageUrl, FAD,
            TAD, FAT, TAT, categories, status, UserId, Username, UserImage, AdminEmail;

    public Product() {

    }



    public Product(String email, String Uid, String address, String condition, String imageUrl, String FAD,
                   String TAD, String FAT, String TAT, String categories, String status, String UserId,
                   String Username, String UserImage, String AdminEmail) {
        this.email = email;
        this.Uid = Uid;
        this.address = address;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.FAD = FAD;
        this.TAD = TAD;
        this.FAT = FAT;
        this.TAT = TAT;
        this.categories = categories;
        this.status = status;
        this.UserId = UserId;
        this.Username = Username;
        this.UserImage = UserImage;
        this.AdminEmail = AdminEmail;

    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return Uid;
    }

    public String getAddress() {
        return address;
    }

    public String getCondition() {
        return condition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFAD() {
        return FAD;
    }

    public String getTAD() {
        return TAD;
    }

    public String getFAT() {
        return FAT;
    }

    public String getTAT() {
        return TAT;
    }

    public String getCategories() {
        return categories;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return Username;
    }

    public String getUserImage() {
        return UserImage;
    }

    public String getAdminEmail() {
        return AdminEmail;
    }

}
