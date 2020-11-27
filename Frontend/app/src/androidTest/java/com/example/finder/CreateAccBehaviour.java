package com.example.finder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.finder.models.UserAccount;
import com.example.finder.views.CreateAccView;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccBehaviour {

    @Rule
    public IntentsTestRule<CreateAccView> mActivityTestRule = new IntentsTestRule<>(CreateAccView.class, false, false);

    private Intent createIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAccView.class);
        UserAccount user = new UserAccount("0", "Bob", "Smith", "email@email.com");
        intent.putExtra("profile", user);
        return intent;
    }

    @Test
    public void firstNameEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.firstNameInput)).perform(replaceText(""));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Field can't be empty"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Field can't be empty")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void firstNameTooLong() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.firstNameInput)).perform(replaceText("ThisNameIsTooLongForTheApp"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("First name too long"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("First name too long")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void firstNameValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.firstNameInput)).perform(replaceText("Bob"));
        try {
            onView(allOf(withId(R.id.firstNameInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void lastNameEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.lastNameInput)).perform(replaceText(""));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Field can't be empty"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Field can't be empty")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void lastNameTooLong() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.lastNameInput)).perform(replaceText("ThisLastNameIsTooLongForTheApp"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Last name too long"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Last name too long")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void lastNameValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.lastNameInput)).perform(replaceText("Smith"));
        try {
            onView(allOf(withId(R.id.lastNameInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void ageEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.ageInput)).perform(replaceText(""));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Field can't be empty"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Field can't be empty")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void ageTooLarge() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.ageInput)).perform(replaceText("153"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Invalid age"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Invalid age")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void ageValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.ageInput)).perform(replaceText("20"));
        try {
            onView(allOf(withId(R.id.ageInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void genderNotSelected() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo(), click());
        onView(withId(R.id.genderSpinner1)).perform(scrollTo());
        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Select Gender"),
                        childAtPosition(
                                allOf(withId(R.id.genderSpinner1),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                3)),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Select Gender")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void genderSelected() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo(), click());
        onView(withId(R.id.genderSpinner1)).perform(scrollTo());
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.genderSpinner1),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatSpinner.perform(scrollTo(), click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Male"),
                        childAtPosition(
                                allOf(withId(R.id.genderSpinner1),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                3)),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Male")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void emailEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.emailInput)).perform(replaceText(""));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Field can't be empty"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Field can't be empty")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void emailInvalid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.emailInput)).perform(replaceText("email"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Invalid email"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Invalid email")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void emailValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.emailInput)).perform(replaceText("email@email.com"));
        try {
            onView(allOf(withId(R.id.emailInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void locationEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.locationInput)).perform(replaceText(""));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Field can't be empty"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Field can't be empty")));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void locationInvalid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.locationInput)).perform(replaceText("InvalidLocation"));
        onView(withId(R.id.create_button)).perform(scrollTo(), click());
        onView(withId(R.id.firstNameInput)).perform(scrollTo());
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Invalid location"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Invalid location")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void locationValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.locationInput)).perform(replaceText("Vancouver"));
        onView(withId(R.id.create_button)).perform(scrollTo(), click());
        onView(withId(R.id.firstNameInput)).perform(scrollTo());
        try {
            onView(allOf(withId(R.id.locationInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void minAgeEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.minAgeInput)).perform(replaceText(""));
        try {
            onView(allOf(withId(R.id.minAgeInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void minAgeTooLarge() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.minAgeInput)).perform(replaceText("153"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Invalid age"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Invalid age")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void minAgeValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.minAgeInput)).perform(replaceText("20"));
        try {
            onView(allOf(withId(R.id.minAgeInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void maxAgeEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.maxAgeInput)).perform(replaceText(""));
        try {
            onView(allOf(withId(R.id.maxAgeInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void maxAgeTooLarge() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.maxAgeInput)).perform(replaceText("153"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Invalid age"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Invalid age")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void maxAgeValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.maxAgeInput)).perform(replaceText("20"));
        try {
            onView(allOf(withId(R.id.maxAgeInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void ageDiffInvalid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.minAgeInput)).perform(replaceText("25"));
        onView(withId(R.id.maxAgeInput)).perform(replaceText("20"));
        onView(withId(R.id.create_button)).perform(click());
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Minimum age must be less than or equal to Maximum age"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textinput_error), withText("Maximum age must be greater than or equal to Minimum age"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Minimum age must be less than or equal to Maximum age")));
            textView2.check(matches(withText("Maximum age must be greater than or equal to Minimum age")));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void ageDiffValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.minAgeInput)).perform(replaceText("20"));
        onView(withId(R.id.maxAgeInput)).perform(replaceText("25"));
        onView(withId(R.id.create_button)).perform(click());
        try {
            onView(allOf(withId(R.id.maxAgeInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void proximityEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.proximityInput)).perform(replaceText(""));
        try {
            onView(allOf(withId(R.id.proximityInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void proximityValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.proximityInput)).perform(replaceText("20"));
        try {
            onView(allOf(withId(R.id.proximityInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void bioEmpty() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.bioInput)).perform(replaceText(""));
        try {
            onView(allOf(withId(R.id.bioInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    @Test
    public void bioTooLong() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.bioInput)).perform(replaceText("This biography is just a jumble of words that will reach the limit of the biography text box so here are some more words to exceed the limit :) :) :) :)"));
        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("Biography too long"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        try {
            textView.check(matches(withText("Biography too long")));
        } catch(Exception err) {
            fail(err.toString());
        }
    }

    @Test
    public void bioValid() {
        Intent intent = createIntent();
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.create_button)).perform(scrollTo());
        onView(withId(R.id.bioInput)).perform(replaceText("Hi! This is a valid biography"));
        try {
            onView(allOf(withId(R.id.bioInput), isDisplayed())).check(matches(hasNoErrorText()));
        } catch(AssertionError err) {
            fail();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<View> hasNoErrorText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has no error text: ");
            }

            @Override
            protected boolean matchesSafely(EditText view) {
                return view.getError() == null;
            }
        };
    }
}
