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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResponseTime {
    private long startTime;
    private final int MAX_RTT = 3000; // ms


    public JSONObject createMockAcc(int id) {
        JSONArray interests = new JSONArray();
        interests.put("hiking");
        interests.put("sleeping");
        interests.put("sining");
        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("lng", 0);
            locationJson.put("lat", 0);
        } catch (JSONException e) {
            Log.d("RTT", "failed to create location json");
            e.printStackTrace();
        }
        JSONObject ageRangeJson = new JSONObject();

        try {
            ageRangeJson.put("min", 10);
            ageRangeJson.put("max", 30);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject preferenceJson = new JSONObject();
        try {
            preferenceJson.put("gender", "Male");
            preferenceJson.put("ageRange", ageRangeJson);
            preferenceJson.put("proximity", 15);
        } catch (JSONException e) {
            Log.d("RTT", "failed to create preference json");
            e.printStackTrace();
        }
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("_id", id);
            userJson.put("firstName", "John");
            userJson.put("lastName", "Smith");
            userJson.put("age", 20);
            userJson.put("gender", "Male");
            userJson.put("email", "yo@bruh.com");
            userJson.put("location", locationJson);
            userJson.put("preferences", preferenceJson);
            userJson.put("interests", interests);
            userJson.put("description", "Nice bio yo");
            Log.d("RTT", userJson.toString());

            return userJson;
        } catch (JSONException e) {
            Log.d("RTT", "failed to create user json");
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void callGetMatches() {
        RequestQueue que = Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());
        final String userId = "110210516334230656872";
        final boolean[] wait = {true};
        final long[] rtt = new long[1];
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                HomeView.HOST_URL + "/match/" + userId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                rtt[0] = System.currentTimeMillis() - startTime;
                Log.d("RTT", "callGetMatches RTT: " + rtt[0]);
                assertTrue(rtt[0] < MAX_RTT);
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
    }

    @Test
    public void callGetMatchesx3() {
        callGetMatches();
        callGetMatches();
        callGetMatches();
    }
}
