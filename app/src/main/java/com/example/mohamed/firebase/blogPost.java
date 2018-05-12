package com.example.mohamed.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by mohamed on 4/1/2018.
 */

public class blogPost {

    private  String imageuri;
    private  String title;
    private  String description;
    private  String profile_pic;
    private  String username;


    public blogPost(String imageuri, String title, String description, String profile_pic, String username) {
        this.imageuri = imageuri;
        this.title = title;
        this.description = description;
        this.profile_pic = profile_pic;
        this.username = username;
    }


    public String getprofile_pic() {
        return profile_pic;
    }

    public void setprofile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public blogPost() {
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
