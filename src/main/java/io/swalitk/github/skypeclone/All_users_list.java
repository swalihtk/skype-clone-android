package io.swalitk.github.skypeclone;

public class All_users_list {
    String username;
    String uid;
    String imageUrl;
    String status;
    public All_users_list(){

    }

    public All_users_list(String username, String uid, String imageUrl, String status) {
        this.username = username;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
