package com.example.finder;

import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.finder.Views.CreateAccView;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityBehaviour {
    @Rule
    public IntentsTestRule<MainActivity> mLoginActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);
    @Test
    public void pushCreateAccount() {
        onView(withId(R.id.create_acc_button)).perform(click());
        intended(hasComponent(CreateAccView.class.getName()));
    }
}
