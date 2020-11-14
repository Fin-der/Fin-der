package com.example.finder.models;

import com.example.finder.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserAccount implements Serializable {
    private String firstName;
    private String lastName;
    private String id;
    private int age;
    private String gender;
    private String email;
    private ArrayList<UserAccount> matches;

    private String[] interest;
    private String location;
    private int minAge;
    private int maxAge;
    private String prefGender;
    private String proximity;
    private String biography;

    // TODO
    // change to first name and last name
    // interest
    //Picture profilePic
    //Location Range
    // location home
    //Some google token stuff?

    public UserAccount(String id, String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
    }

    public UserAccount(String id, String firstName, String lastName, String email, int age, String gender, ArrayList<UserAccount> matches) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.matches = matches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return Integer.toString(age);
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public void setMatches(ArrayList<UserAccount> list) {
        this.matches = list;
    }

    public ArrayList<UserAccount> getMatches() {
        return this.matches;
    }

    public String getNumMatches() {
        return Integer.toString(matches.size());
    }

}
