package com.example.finder;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.models.UserAccount;
import com.example.finder.views.HomeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ResponseTime {
    private long startTime;
    private final int MAX_RTT = 1000;
    private UserAccount user1;
    private UserAccount user2;
    private Context context;

    private void initUsers() {
        user1 = UserAccGenerator.createFullAcc("1");
        user2 = UserAccGenerator.createFullAcc("2");
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserAccGenerator.deleteAcc("1", context);
        UserAccGenerator.deleteAcc("2", context);
        UserAccGenerator.createAccBackend(user1, context);
        UserAccGenerator.createAccBackend(user2, context);
    }

    private long callGetMatches(RequestQueue que) {
        final String userId = "1";
        final boolean[] wait = {true};
        final long[] rtt = new long[1];
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                HomeView.HOST_URL + "/match/" + userId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                rtt[0] = System.currentTimeMillis() - startTime;
                wait[0] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fail(error.toString());
            }
        });
        startTime = System.currentTimeMillis();
        que.add(req);
        while (wait[0])
            Thread.yield();
        return rtt[0];
    }

    private long callGetRecentConversation(RequestQueue que, final String roomId) {
        final boolean[] wait = {true};
        final long[] rtt = new long[1];
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                HomeView.HOST_URL + "/room/" + roomId + "/0/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                rtt[0] = System.currentTimeMillis() - startTime;
                wait[0] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fail(error.toString());
            }
        });
        startTime = System.currentTimeMillis();
        que.add(req);
        while (wait[0])
            Thread.yield();
        return rtt[0];
    }

    @Test
    public void callGetMatchesRTT() {
        initUsers();
        try {
            RequestQueue que = Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());
            for (int i = 0; i < MAX_RTT; i++) {
                Log.d("RTT_GETMATCHES", String.valueOf(callGetMatches(que)));
            }
        } catch (Exception e) {
            UserAccGenerator.deleteAcc(user1.getId(), context);
            UserAccGenerator.deleteAcc(user2.getId(), context);
            fail(e.toString());
        } finally {
            UserAccGenerator.deleteAcc(user1.getId(), context);
            UserAccGenerator.deleteAcc(user2.getId(), context);
        }

    }

    @Test
    public void callGetRecentConversationRTT() throws JSONException {
        initUsers();
        final RequestQueue que = Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());
        final String roomId = UserAccGenerator.initChatRoom(user1.getId(), user2.getId(), que);
        JSONObject body = new JSONObject();
        body.put("userId", user1.getId());

        try {
            for (int i = 0; i < MAX_RTT; i++)
                Log.d("RTT_RECENTCONVO", String.valueOf(callGetRecentConversation(que, roomId)));
        } catch (Exception err) {
            fail(err.toString());
        } finally {
            UserAccGenerator.deleteAcc(user2.getId(), context);
            UserAccGenerator.deleteAcc(user1.getId(), context);
        }
    }
}
