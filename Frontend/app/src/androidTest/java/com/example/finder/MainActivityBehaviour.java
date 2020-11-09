package com.example.finder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.finder.views.CreateAccView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityBehaviour {
    @Rule
    public IntentsTestRule<MainActivity> mLoginActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);
    @Test
    public void pushCreateAccount() {
        onView(withId(R.id.create_acc_button)).perform(click());
        try {
            intended(hasComponent(CreateAccView.class.getName()));
        } catch (AssertionError e) {
            fail();
        }

    }
}
