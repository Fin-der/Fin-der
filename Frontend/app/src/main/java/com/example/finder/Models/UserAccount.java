package com.example.finder.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserAccount implements Serializable {
    private String userName;
    private String id;
    private int age;
    private String gender;
    private String email;

    private ArrayList<UserAccount> matches;
    // TODO
    // change to first name and last name
    // interest
    //Picture profilePic
    //Location Range
    // location home
    //Some google token stuff?

    public UserAccount(String userName, String id, String gender) {
        this.userName = userName;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id;}

    public String getUserName() {
        return userName;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

}
