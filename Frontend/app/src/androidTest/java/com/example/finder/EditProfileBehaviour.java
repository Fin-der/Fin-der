package com.example.finder;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.finder.models.UserAccount;
import com.example.finder.views.ProfileView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
        onView(withId(R.id.email_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.location_profile)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.proximity_profile)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.min_age_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.min_age_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.bio_profile)).perform(scrollTo()).check(matches(isDisplayed()));
    }



}
