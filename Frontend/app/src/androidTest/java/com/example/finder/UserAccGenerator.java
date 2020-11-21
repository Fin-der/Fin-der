package com.example.finder;

import com.example.finder.models.UserAccount;

import java.util.ArrayList;

public class UserAccGenerator {
     static public UserAccount createFullAcc() {
         String id = "0";
         String firstName = "Jacky";
         String lastName = "Smith";
         String email = "Email";
         int age = 5;
         String gender = "Male";
         String location = "Vancouver";
         String prefGender = "Female";
         int minAge = 0;
         int maxAge = age + 5;
         int prox = 5;
         String[] interest = new String[]{"a", "b", "c"};
         String bio = "hello";
         ArrayList<UserAccount> friend = new ArrayList<>();
         friend.add(new UserAccount("1", "Jack", "Frost"));
         ArrayList<UserAccount> matches = new ArrayList<>();

         UserAccount user = new UserAccount(id, firstName, lastName, email, age, gender,
                 location, prefGender, minAge, maxAge, prox, interest, bio);
         user.setMatches(matches);
         user.setFriendMatches(friend);
         return user;
    }
}
