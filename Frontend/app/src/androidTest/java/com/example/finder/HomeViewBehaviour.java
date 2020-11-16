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

import com.example.finder.models.UserAccount;
import com.example.finder.views.CreateAccView;
import com.example.finder.views.HomeView;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeViewBehaviour {
    @Rule
    public ActivityScenarioRule<HomeView> activityRule
            = new ActivityScenarioRule<>(createIntent());
    @Rule
    public IntentsTestRule<MainActivity> mLoginActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);

    Intent createIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeView.class);
        UserAccount user = new UserAccount("0", "Bob", "Smith");
        ArrayList<UserAccount> friends = new ArrayList<>();
        friends.add(new UserAccount("1", "Jack", "Lantern"));
        user.setFriendMatches(friends);
        user.setMatches(new ArrayList<UserAccount>());
        intent.putExtra("profile", user);
        return intent;
    }

    @Test
    public void countMessageBoard() {
        try {
            onView(withId(R.layout.msgboard_profile)).check(matches(isDisplayed()));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void editProfileView() {
        onView(withId(R.id.home_profileBtn)).perform(click());
        try {
            intended(hasComponent(CreateAccView.class.getName()));
        } catch (AssertionError e) {
            fail();
        }
    }

}
