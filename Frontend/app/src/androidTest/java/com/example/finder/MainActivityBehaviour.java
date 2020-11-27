package com.example.finder;

import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityBehaviour {
    @Rule
    public IntentsTestRule<MainActivity> mLoginActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);
}
