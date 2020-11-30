package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.models.UserAccount;
import com.example.finder.views.CreateAccView;
import com.example.finder.views.HomeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    /*Tag for checking Logcat*/
    private final static String TAG = "MainActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    private String FCM_token;
    private UserAccount profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Listener for FCM tokens
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String FCM_token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, FCM_token);
                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This function creates the google sign in pop up
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(final GoogleSignInAccount account) {
        if (account == null) {
            Log.d(TAG, "There is no user signed in!");
        } else {
            Log.d(TAG, "Preferred Name: " + account.getDisplayName());
            Log.d(TAG, "Email: " + account.getEmail());
            Log.d(TAG, "Given Name: " + account.getGivenName());
            Log.d(TAG, "Family Name: " + account.getFamilyName());
            Log.d(TAG, "Display URI: " + account.getPhotoUrl());

            JSONObject loginInfo = new JSONObject();        // json object for id to check if user is registered
            try {
                loginInfo.put("_id", account.getId());
                Log.d(TAG, loginInfo.toString());
            } catch (JSONException e) {
                Log.d(TAG, "failed to create json");
                e.printStackTrace();
            }
            final RequestQueue reqQueue = Volley.newRequestQueue(MainActivity.this);
            final String url = HomeView.HOST_URL;
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url + "/users/" + account.getId(), loginInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    profile = parseAccount(response);   // parse response information if user is registered
                    if ((account.getPhotoUrl() == null && profile.getpfpUrl() != null)
                            || (account.getPhotoUrl() != null && profile.getpfpUrl() == null)
                            || !String.valueOf(account.getPhotoUrl()).equals(profile.getpfpUrl())) { // update the user photo if changed in google
                        profile.setpfpUrl(account.getPhotoUrl());
                        JSONObject accountJson = packJson(profile);
                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url + "/users/" + account.getId(), accountJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent home = new Intent(MainActivity.this, HomeView.class);
                                home.putExtra("profile", profile);
                                Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                                startActivity(home);    // sign in upon successful photo change
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        });
                        reqQueue.add(jsonRequest);
                    } else {
                        Intent home = new Intent(MainActivity.this, HomeView.class);
                        home.putExtra("profile", profile);
                        Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                        startActivity(home);    // sign in upon if response received and no change in photo
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
                    if (error instanceof ServerError) { // this server error is thrown if user is not registered
                        UserAccount profile = new UserAccount(account.getId(), account.getGivenName(), account.getFamilyName(), account.getEmail());
                        profile.setpfpUrl(account.getPhotoUrl());
                        Intent create = new Intent(MainActivity.this, CreateAccView.class);
                        create.putExtra("profile", profile);
                        create.putExtra("FCMToken", FCM_token);
                        startActivity(create);  // go to the create account screen
                    }
                }
            });
            reqQueue.add(jsonReq);
        }
    }

    /**
     * This function parses the json response from the backend
     * @param response the JSONObject returned from the backend
     * @return the UserAccount associated with the current google account
     */
    private UserAccount parseAccount(JSONObject response) {
        UserAccount profile = null;
        try {
            JSONObject account = (JSONObject) response.get("user");
            String id = account.getString("_id");
            String firstName = account.getString("firstName");
            String lastName = account.getString("lastName");
            int age = account.getInt("age");
            String gender = account.getString("gender");
            String email = account.getString("email");
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> address = null;
            try {
                // get a list of addresses based on the longitude and latitude
                address = geocoder.getFromLocation(Double.parseDouble(account.getJSONObject("geoLocation").getJSONObject("lat").getString("$numberDecimal")),
                        Double.parseDouble(account.getJSONObject("geoLocation").getJSONObject("lng").getString("$numberDecimal")), 1);
            } catch (IOException e) {
                Log.d(TAG, "failed to get location");
                e.printStackTrace();
            }
            String location = address.get(0).getLocality(); // get the locality (city)
            String prefGender = account.getJSONObject("preferences").getString("gender");
            int minAge = account.getJSONObject("preferences").getJSONObject("ageRange").getInt("min");
            int maxAge = account.getJSONObject("preferences").getJSONObject("ageRange").getInt("max");
            int proximity = account.getJSONObject("preferences").getInt("proximity");
            JSONArray interestArr = account.getJSONArray("interests");
            String[] interest = new String[interestArr.length()];
            for (int i = 0; i < interestArr.length(); i++) {
                interest[i] = interestArr.getString(i);
            }
            String biography = account.getString("description");
            Uri pfp = Uri.parse(account.getString("profileURL"));
            profile = new UserAccount(id, firstName, lastName, email);
            profile.setAge(age);
            profile.setGender(gender);
            profile.setLocation(location);
            profile.setPrefGender(prefGender);
            profile.setMinAge(minAge);
            profile.setMaxAge(maxAge);
            profile.setProximity(proximity);
            profile.setInterest(interest);
            profile.setBiography(biography);
            profile.setpfpUrl(pfp);
        } catch (JSONException e) {
            Log.d(TAG, "failed to parse json");
            e.printStackTrace();
        }
        return profile;
    }

    /**
     * This function packs the json to send to the backend to update the photo
     * @param user the UserAccount to pack into a json
     * @return JSONObject to send to the backend
     */
    private JSONObject packJson(UserAccount user) {
        JSONArray interests = new JSONArray();
        interests.put(user.getInterest()[0]);
        interests.put(user.getInterest()[1]);
        interests.put(user.getInterest()[2]);
        JSONObject locationJson = new JSONObject();
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        double longitude = 0;
        double latitude = 0;
        try {
            list = geocoder.getFromLocationName(user.getLocation(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "something wrong finding location");
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            longitude = address.getLongitude();
            latitude = address.getLatitude();
        }
        try {
            locationJson.put("lng", longitude);
            locationJson.put("lat", latitude);
        } catch (JSONException e) {
            Log.d(TAG, "failed to create location json");
            e.printStackTrace();
        }
        JSONObject ageRangeJson = new JSONObject();
        try {
            ageRangeJson.put("min", user.getMinAge());
            ageRangeJson.put("max", user.getMaxAge());
        } catch (JSONException e) {
            Log.d(TAG, "failed to create age range json");
            e.printStackTrace();
        }
        JSONObject preferenceJson = new JSONObject();
        try {
            preferenceJson.put("gender", user.getPrefGender());
            preferenceJson.put("ageRange", ageRangeJson);
            preferenceJson.put("proximity", user.getProximity());
        } catch (JSONException e) {
            Log.d(TAG, "failed to create preference json");
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
        } catch (JSONException e) {
            Log.d(TAG, "failed to create user json");
            e.printStackTrace();
        }
        Log.d(TAG, userJson.toString());
        return userJson;
    }
}