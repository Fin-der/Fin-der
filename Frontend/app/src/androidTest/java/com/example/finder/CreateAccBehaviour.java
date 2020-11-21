package com.example.finder;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.finder.models.UserAccount;
import com.example.finder.views.CreateAccView;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccBehaviour {

    @Rule
    public ActivityTestRule<CreateAccView> mActivityTestRule = new ActivityTestRule<>(CreateAccView.class, false, false);

    Intent createIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAccView.class);
        UserAccount user = new UserAccount("0", "Bob", "Smith", "email@email.com");
        user.setFriendMatches(new ArrayList<UserAccount>());
        user.setMatches(new ArrayList<UserAccount>());
        intent.putExtra("profile", user);
        return intent;
    }

//    @Test
//    public void checkError() {
//        Intent intent = createIntent();
//        mActivityTestRule.launchActivity(intent);
//        onView(withId(R.id.genderSpinner1)).perform(click());
//        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
//        onView((withId(R.id.))).check(matches(withText(containsString("Male"))));
//    }

    @Test
    public void checkAgeError1() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo()).perform(click());
        onView((withId(R.id.editTextAge))).perform(scrollTo()).check(matches(hasErrorText("Field can't be empty")));
    }

    @Test
    public void checkAgeError2() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.ageInput)).perform(replaceText("153"));
        onView((withId(R.id.ageInput))).perform(scrollTo()).check(matches(hasErrorText("Invalid age")));
    }

    @Test
    public void checkAgeError3() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.ageInput)).perform(replaceText("20"));
        onView((withId(R.id.ageInput))).perform(scrollTo()).check(matches(hasErrorText("Invalid age")));
    }
}
