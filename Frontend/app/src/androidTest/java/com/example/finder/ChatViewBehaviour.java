package com.example.finder;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.finder.models.UserAccount;
import com.example.finder.views.ChatView;

import org.junit.Rule;
import org.junit.Test;

public class ChatViewBehaviour {
    @Rule
    public IntentsTestRule<ChatView> activityRule
            = new IntentsTestRule<>(ChatView.class, false, false);

    public void startTwoUsers() {
        Intent intent = new Intent();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserAccGenerator.deleteAcc("1", context);
        UserAccGenerator.deleteAcc("2", context);
        UserAccount user = UserAccGenerator.createFullAcc("1");
        UserAccount user2 = UserAccGenerator.createFullAcc("2");
        UserAccGenerator.createAccBackend(user, context);
        UserAccGenerator.createAccBackend(user2, context);
        UserAccGenerator.friendTwoAccs(user.getId(), user2.getId(), context);
        intent.putExtra("user", user);
        intent.putExtra("friend", user2);
        intent.putExtra("chatterName", user2.getFirstName() + " " + user2.getLastName());
        activityRule.launchActivity(intent);
    }

    @Test
    public void testOpen() {
        startTwoUsers();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
