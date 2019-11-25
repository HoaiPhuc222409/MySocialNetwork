package com.example.mysocialnetwork;

public class FindFriends {
    public String profileimage, fullName, status;

    public FindFriends(){

    }

    public FindFriends(String profilename, String fullName, String status) {
        this.profileimage = profilename;
        this.fullName = fullName;
        this.status = status;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profilename) {
        this.profileimage = profilename;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
