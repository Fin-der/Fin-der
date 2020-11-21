package com.example.finder;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.finder.models.UserAccount;
import com.example.finder.views.MatchView;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;


public class MatchViewBehaviour {
    @Rule
    public IntentsTestRule<MatchView> activityRule
            = new IntentsTestRule<>(MatchView.class, false, false);

    public void startActivity() {
        Intent intent = new Intent();
        UserAccount user = UserAccGenerator.createFullAcc();
        user.getMatches().add(new UserAccount("0", "Danny", "Phantom"));
        user.getMatches().get(0).setBiography("Biography");
        user.getMatches().add(new UserAccount("1", "Nick", "Ng"));
        user.getMatches().get(1).setBiography("Balrog");
        intent.putExtra("profile", user);
        activityRule.launchActivity(intent);
    }

    @Test
    public void oneMatch() {
        startActivity();
        onView(allOf(withId(R.id.match_name), isDisplayed())).check(matches(withText("Danny Phantom")));
        onView(allOf(withId(R.id.match_bio), isDisplayed())).check(matches(withText("Biography")));
    }

    @Test
    public void twoMatches() {
        startActivity();
        onView(withId(R.id.match_pager)).perform(swipeLeft());
        onView(allOf(withId(R.id.match_name), isDisplayed())).check(matches(withText("Nick Ng")));
        onView(allOf(withId(R.id.match_bio), isDisplayed())).check(matches(withText("Balrog")));
    }


}
