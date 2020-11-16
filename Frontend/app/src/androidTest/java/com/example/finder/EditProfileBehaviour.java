package com.example.finder;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.finder.models.UserAccount;
import com.example.finder.views.CreateAccView;
import com.example.finder.views.HomeView;
import com.example.finder.views.ProfileView;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.supportsInputMethods;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

public class EditProfileBehaviour {
    @Rule
    public IntentsTestRule<ProfileView> activityRule
            = new IntentsTestRule<>(ProfileView.class, false, false);

    Intent createFullAcc() {
        Intent intent = new Intent();
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
        intent.putExtra("profile", user);

        return intent;
    }

    @Test
    public void openActivity() {
        Intent intent = createFullAcc();
        activityRule.launchActivity(intent);
    }



}
