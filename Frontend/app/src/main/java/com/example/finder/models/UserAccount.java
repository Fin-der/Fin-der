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
    private int proximity;
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

    public UserAccount(String id, String firstName, String lastName, String email, int age, String gender, String location,
                       String prefGender, int minAge, int maxAge, int proximity, String[] interest, String biography, ArrayList<UserAccount> matches) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.matches = matches;
        this.interest = interest;
        this.location = location;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.prefGender = prefGender;
        this.proximity = proximity;
        this.biography = biography;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return Integer.toString(age);
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrefGender(String prefGender) {
        this.prefGender = prefGender;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }

    public void setInterest(String[] interest) {
        this.interest = interest;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

}
