package com.example.finder.models;

import java.io.Serializable;
import java.util.ArrayList;

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
    private String matchId;

    private int[] spinnerIndex;

    private ArrayList<UserAccount> friendMatches;

    // TODO
    //Picture profilePic

    public UserAccount(String id, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public UserAccount(String id, String firstName, String lastName, String email/*, String photo*/) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrefGender() {
        return prefGender;
    }

    public void setPrefGender(String prefGender) {
        this.prefGender = prefGender;
    }

    public String getMinAge() {
        return Integer.toString(minAge);
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public String getMaxAge() {
        return Integer.toString(maxAge);
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getProximity() {
        return Integer.toString(proximity);
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }

    public String[] getInterest() {
        return this.interest;
    }

    public void setInterest(String[] interest) {
        this.interest = interest;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String id) {
        this.matchId = id;
    }

    public void setFriendMatches(ArrayList<UserAccount> list) {
        this.friendMatches = list;
    }

    public ArrayList<UserAccount> getFriendMatches() {
        return this.friendMatches;
    }

    public void setSpinnerIndex(int[] spinnerIndex) {
        this.spinnerIndex = spinnerIndex;
    }

    public int[] getSpinnerIndex() {
        return this.spinnerIndex;
    }
}
