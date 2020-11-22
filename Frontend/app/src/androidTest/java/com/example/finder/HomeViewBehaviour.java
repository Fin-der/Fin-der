package com.example.finder;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.finder.models.UserAccount;
import com.example.finder.views.HomeView;
import com.example.finder.views.MatchView;
import com.example.finder.views.ProfileView;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeViewBehaviour {
    @Rule
    public IntentsTestRule<HomeView> activityRule
            = new IntentsTestRule<>(HomeView.class, false, false);

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
        Intent intent = createIntent();
        activityRule.launchActivity(intent);
        try {
            onView(withId(R.id.msgboard_profilename)).check(matches(withText("Jack")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void editProfileView() {
        Intent intent = createIntent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.home_profileBtn)).perform(click());
        try {
            intended(hasComponent(ProfileView.class.getName()));
        } catch (AssertionError e) {
            fail();
        }
    }

    @Test
    public void findMatchesView() {
        Intent intent = createIntent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.home_FindMatchBtn)).perform(click());
        try {
            intended(hasComponent(MatchView.class.getName()));
        } catch (AssertionError e) {
            fail();
        }
    }

}
