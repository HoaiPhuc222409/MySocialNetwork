package com.example.mysocialnetwork;


public class Comments {
    public String comment, date, time, userName, image;

    public Comments(){

    }

    public Comments(String comment, String date, String time, String userName, String image) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.userName = userName;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
