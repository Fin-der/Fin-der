package com.example.finder.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserAccount implements Serializable {
    private String userName;
    public String id;
    private int age;
    private String gender;
    private ArrayList<UserAccount> matches;

    //Picture profilePic
    //Location Range
    //Some google token stuff?

    public UserAccount(String userName, String id, String gender) {
        this.userName = userName;
        this.id = id;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public int getAge() {
        return age;
    }

}
