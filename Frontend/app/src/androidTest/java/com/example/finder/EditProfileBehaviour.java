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
        UserAccount user = UserAccGenerator.createFullAcc();
        intent.putExtra("profile", user);

        return intent;
    }

    @Test
    public void startActivity() {
        Intent intent = createFullAcc();
        activityRule.launchActivity(intent);
    }

    @Test
    public void openActivity() {
        startActivity();
    }

    @Test
    public void checkName() {
        startActivity();
        onView(withId(R.id.fullNameText)).check(matches(withText("Jacky Smith")));
    }

    @Test
    public void checkMatchNum() {
        startActivity();
        onView(withId(R.id.number_matches)).check(matches(withText("0")));
    }

    @Test
    public void checkDetails() {
        startActivity();
        onView(withId(R.id.first_name_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.last_name_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.age_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.location_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.min_age_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.min_age_profile)).check(matches(isDisplayed()));
    }



}
