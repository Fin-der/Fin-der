package com.example.finder;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.models.UserAccount;
import com.example.finder.views.ChatView;
import com.example.finder.views.HomeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;


public class ChatViewBehaviour {
    private UserAccount user2;
    private String chatterName;
    private Context context;

    @Rule
    public IntentsTestRule<ChatView> activityRule
            = new IntentsTestRule<>(ChatView.class, false, false);

    public void startTwoUsers() {
        Intent intent = new Intent();
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserAccGenerator.deleteAcc("1", context);
        UserAccGenerator.deleteAcc("2", context);
        UserAccount user = UserAccGenerator.createFullAcc("1");
        user2 = UserAccGenerator.createFullAcc("2");
        UserAccGenerator.createAccBackend(user, context);
        UserAccGenerator.createAccBackend(user2, context);
        UserAccGenerator.friendTwoAccs(user.getId(), user2.getId(), context);
        intent.putExtra("user", user);
        intent.putExtra("friend", user2);
        chatterName = user2.getFirstName() + " " + user2.getLastName();
        intent.putExtra("chatterName", chatterName);
        activityRule.launchActivity(intent);
    }

    private void user2SendMsg(final RequestQueue que, final UserAccount user, final String message) {
        JSONObject obj = new JSONObject();
        final int check[] = {0};
        try {
            obj.put("userId", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String URI = HomeView.HOST_URL + "/room/";
        System.out.println(URI);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URI, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String roomId = response.getJSONArray("conversation").getJSONObject(0).getString("chatRoomId");
                    JSONObject msg = new JSONObject();
                    msg.put("messageText", message);
                    msg.put("userId", user.getId());
                    msg.put("roomId", roomId);
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                            URI + roomId + "/" + user.getId() + "/message",
                            msg,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("Response" + response.toString());
                                    check[0] = 1;
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("PostMessage: " + error.toString());
                            error.printStackTrace();
                            check[0] = 2;
                        }
                    });
                    que.add(req);
                } catch (JSONException e) {
                    e.printStackTrace();
                    check[0] = 3;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                check[0] = 4;
            }
        });
        que.add(req);
        while (check[0] == 0)
            Thread.yield();
    }

    @Test
    public void testChatterName() {
        startTwoUsers();
        ViewInteraction textView = onView(
                allOf(withText(chatterName),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        try {
            textView.check(matches(withText(chatterName)));
        } catch(Exception err) {
            fail(err.toString());
        }

    }

    @Test
    public void testuser1Message() {
        startTwoUsers();
        final String testText = "test";
        onView(withId(R.id.edittext_chatbox)).perform(replaceText(testText));
        onView(withId(R.id.button_chatbox_send)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_message_body), withText(testText),
                        withParent(withParent(withId(R.id.reyclerview_message_list))),
                        isDisplayed()));
        try {
            assert(textView != null);
        } catch (AssertionError error) {
            fail(error.toString());
        }
    }

    @Test
    public void testReceive1Message() {
        startTwoUsers();
        RequestQueue que = Volley.newRequestQueue(context);
        final String testText = "hello";
        user2SendMsg(que, user2, testText);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_message_body), withText(testText),
                        withParent(withParent(withId(R.id.reyclerview_message_list))),
                        isDisplayed()));
        try {
            assert(textView != null);
        } catch (AssertionError err) {
            fail(err.toString());
        }
    }

    @Test
    public void goHomeAndBack() throws InterruptedException {
        startTwoUsers();
        Thread.sleep(2000);
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_message_body),
                        withParent(withParent(withId(R.id.reyclerview_message_list))),
                        isDisplayed()));
        try {
            assert(textView != null);
        } catch (AssertionError err) {
            fail(err.toString());
        }
    }

    private void create25Msgs() throws InterruptedException {
        final int CAP = 25;
        startTwoUsers();
        RequestQueue que = Volley.newRequestQueue(context);
        for (int i = 0; i < CAP; i++) {
            user2SendMsg(que, user2, String.valueOf(i));
        }
        Thread.sleep(10000);
    }

    @Test
    public void checkoldMsgs() throws InterruptedException {
        create25Msgs();
        final int SWIPES = 5;
        for (int i = 0; i < SWIPES; i++) {
            onView(withId(R.id.reyclerview_message_list))
                    .perform(swipeDown());
        }
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_message_body),
                        withParent(withParent(withId(R.id.reyclerview_message_list))),
                        isDisplayed()));
        try {
            assert(textView != null);
        } catch (AssertionError err) {
            fail(err.toString());
        }
    }

}
