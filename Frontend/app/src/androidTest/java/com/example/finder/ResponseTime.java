package com.example.finder;

import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.views.HomeView;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ResponseTime {
    private long startTime;
    private final int MAX_RTT = 1000;

    private long callGetMatches(RequestQueue que) {
        final String userId = "0";
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
                fail();
            }
        });
        startTime = System.currentTimeMillis();
        que.add(req);
        while (wait[0])
            Thread.yield();
        return rtt[0];
    }

    private long callGetRecentConversation(RequestQueue que) {
        final String roomId = "6b30126e2cc047a3858f547cc1ca1dde";
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
                fail();
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
        try {
            RequestQueue que = Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());
            for (int i = 0; i < MAX_RTT; i++) {
                Log.d("RTT_GETMATCHES", String.valueOf(callGetMatches(que)));
            }
        } catch (Exception e) {
            fail(e.toString());
        }

    }

    @Test
    public void callGetRecentConversationRTT() {
        try {
            RequestQueue que = Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());
            for (int i = 0; i < MAX_RTT; i++) {
                Log.d("RTT_RECENTCONVO", String.valueOf(callGetRecentConversation(que)));
            }
        } catch (Exception err) {
            fail(err.toString());
        }
    }
}
