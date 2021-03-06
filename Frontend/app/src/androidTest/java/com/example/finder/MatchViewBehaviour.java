package com.example.finder;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.finder.models.UserAccount;
import com.example.finder.views.MatchView;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;


public class MatchViewBehaviour {
    @Rule
    public IntentsTestRule<MatchView> activityRule
            = new IntentsTestRule<>(MatchView.class, false, false);

    public void startActivity() {
        Intent intent = new Intent();
        UserAccount user = UserAccGenerator.createFullAcc("0");
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
        try {
            onView(allOf(withId(R.id.match_name), isDisplayed())).check(matches(withText("Danny Phantom")));
            onView(allOf(withId(R.id.match_bio), isDisplayed())).check(matches(withText("Biography")));
        } catch (AssertionError e) {
            fail();
        }
    }

    @Test
    public void twoMatches() {
        startActivity();
        onView(withId(R.id.match_pager)).perform(swipeUp());
        try {
            onView(allOf(withId(R.id.match_name), isDisplayed())).check(matches(withText("Nick Ng")));
            onView(allOf(withId(R.id.match_bio), isDisplayed())).check(matches(withText("Balrog")));
        } catch (AssertionError e) {
            fail();
        }
    }

    @Test
    public void clickApprove() throws InterruptedException {
        startActivity();
        onView(withId(R.id.match_accept)).perform(click());
        Thread.sleep(500);
        try {
            onView(withText(R.string.match_approve_err))
                    .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        } catch (Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void clickDecline() {
        startActivity();
        onView(withId(R.id.match_deny)).perform(click());
        try {
            onView(withText(R.string.match_deny_err))
                    .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        } catch (Exception err) {
            fail(err.toString());
        }
    }

}
