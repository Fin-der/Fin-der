package com.example.finder.Models;

import java.util.ArrayList;

public class UserAccount {
    private String userName;
    private String id;
    private int age;
    private Gender gender;
    private ArrayList<UserAccount> matches;

    //Picture profilePic
    //Location Range
    //Some google token stuff?

    public UserAccount(String userName, String id, Gender gender) {
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

    public static enum Gender{MALE, FEMALE, OTHER};
}
