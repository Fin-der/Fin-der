package com.example.finder;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.models.UserAccount;
import com.example.finder.views.HomeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserAccGenerator {
     static public UserAccount createFullAcc(String userId) {
         String id = userId;
         String firstName = "Jacky";
         String lastName = "Smith";
         String email = "Email@email.com";
         int age = 5;
         String gender = "Male";
         String location = "Vancouver";
         String prefGender = "Female";
         int minAge = 17;
         int maxAge = age + 5;
         int prox = 20;
         String[] interest = new String[]{"Hockey", "Burger", "Cook"};
         String bio = "debug account";
         ArrayList<UserAccount> friend = new ArrayList<>();
         friend.add(new UserAccount("1", "Jack", "Frost"));
         ArrayList<UserAccount> matches = new ArrayList<>();

         UserAccount user = new UserAccount(id, firstName, lastName, email);
         user.setAge(age);
         user.setGender(gender);
         user.setLocation(location);
         user.setPrefGender(prefGender);
         user.setMinAge(minAge);
         user.setMaxAge(maxAge);
         user.setProximity(prox);
         user.setInterest(interest);
         user.setBiography(bio);
         user.setMatches(matches);
         user.setFriendMatches(friend);
         return user;
    }

    static public void friendTwoAccs(String id1, String id2, Context context) {
        final String HOST_URL = HomeView.HOST_URL + "/match/";
        final boolean[] check = {true, true};
        RequestQueue que = Volley.newRequestQueue(context);
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT, HOST_URL + "approve/" + id1 + "/" + id2,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                check[0] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                check[0] = false;
            }
        });
        JsonObjectRequest req2 = new JsonObjectRequest(
                Request.Method.PUT, HOST_URL + "approve/" + id2 + "/" + id1,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                check[1] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                check[1] = false;
                error.printStackTrace();
            }
        });
        que.add(req);
        que.add(req2);
        while (check[0] || check[1]) {
            Log.d("UserAccgen", "Waiting to friend");
            Thread.yield();
        }

    }

    public static String initChatRoom(String id1, String id2, RequestQueue que) throws JSONException {
         JSONObject body = new JSONObject();
         JSONArray arr = new JSONArray();
         arr.put(id1);
         arr.put(id2);
         body.put("userIds", arr);
         final String[] roomId = {null};
         JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                 HomeView.HOST_URL + "/room/initiate/", body, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response) {
                 JSONObject room = null;
                 try {
                     room = (JSONObject) response.get("chatRoom");
                     roomId[0] = (String) room.get("chatRoomId");
                     Log.d("roomId", roomId[0]);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                Log.d("UserAccGen", error.toString());
             }
         });
         que.add(req);
         while (roomId[0] == null)
             Thread.yield();
         return roomId[0];
    }

    public static void deleteAcc(String id, Context context) {
        RequestQueue que = Volley.newRequestQueue(context);
        final String HOST_URL = HomeView.HOST_URL + "/users/" + id;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, HOST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Deleted Account");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        que.add(req);
    }

    public static void createAccBackend(UserAccount user, Context context) {
        JSONObject packedJson = packJson(user);
        final String HOST_URL = HomeView.HOST_URL + "/users/";
        final boolean[] check = {true};
        RequestQueue que = Volley.newRequestQueue(context);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, HOST_URL, packedJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createAccBackend", "Create acc correctly");
                check[0] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CreateAccBackend", "Create acc failed");
                check[0] = false;
            }
        });
        que.add(req);
        while (check[0]) {
            Thread.yield();
            Log.d("createAccBackend", "Waiting to create Acc");
        }
    }

    private static JSONObject packJson(UserAccount user) {
        JSONArray interests = new JSONArray();
        String[] interestResult = user.getInterest();

        interests.put(interestResult[0]);
        interests.put(interestResult[1]);
        interests.put(interestResult[2]);
        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("lng", "0");
            locationJson.put("lat", "0");
        } catch (JSONException e) {
            Log.d("UserAccountGen", "failed to create location json");
            e.printStackTrace();
        }
        JSONObject ageRangeJson = new JSONObject();
        try {
                ageRangeJson.put("min", 0);
                ageRangeJson.put("max", 99);
        } catch (JSONException e) {
            Log.d("UserAccountGen", "failed to create age range json");
            e.printStackTrace();
        }
        JSONObject preferenceJson = new JSONObject();
        try {
            preferenceJson.put("gender", user.getPrefGender());
            preferenceJson.put("ageRange", ageRangeJson);
            preferenceJson.put("proximity", user.getProximity());
        } catch (JSONException e) {
            Log.d("UserAccountGen", "failed to create preference json");
            e.printStackTrace();
        }
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("_id", user.getId());
            userJson.put("firstName", user.getFirstName());
            userJson.put("lastName", user.getLastName());
            userJson.put("age", user.getAge());
            userJson.put("gender", user.getGender());
            userJson.put("email", user.getEmail());
            userJson.put("geoLocation", locationJson);
            userJson.put("preferences", preferenceJson);
            userJson.put("interests", interests);
            userJson.put("description", user.getBiography());
            userJson.put("profileURL", user.getpfpUrl());
            Log.d("UserAccountGen", userJson.toString());
        } catch (JSONException e) {
            Log.d("UserAccountGen", "failed to create user json");
            e.printStackTrace();
        }
        Log.d("UserAccountGen", userJson.toString());
        return userJson;
    }
}
