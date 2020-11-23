package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
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

        findViewById(R.id.skipHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount profile = new UserAccount("id", "Nicholas", "Ng", "email@email.com");
                Intent intent = new Intent(MainActivity.this, HomeView.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        findViewById(R.id.create_acc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount profile = new UserAccount("id", "Nicholas", "Ng", "email@email.com");
                Intent createAcc = new Intent(MainActivity.this, CreateAccView.class);
                createAcc.putExtra("profile", profile);
                startActivity(createAcc);
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
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

            JSONObject loginInfo = new JSONObject();
            try {
                loginInfo.put("_id", account.getId());
                Log.d(TAG, loginInfo.toString());
            } catch (JSONException e) {
                Log.d(TAG, "failed to create json");
                e.printStackTrace();
            }
            RequestQueue reqQueue = Volley.newRequestQueue(MainActivity.this);
            String url = HomeView.HOST_URL;
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url + "/users/" + account.getId(), loginInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    profile = parseAccount(response);
                    Intent home = new Intent(MainActivity.this, HomeView.class);
                    home.putExtra("profile", profile);
                    startActivity(home);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
                    if (error instanceof ServerError) {
                        UserAccount profile = new UserAccount(account.getId(), account.getGivenName(), account.getFamilyName(), account.getEmail());
                        profile.setpfpUrl(account.getPhotoUrl());
                        Intent create = new Intent(MainActivity.this, CreateAccView.class);
                        create.putExtra("profile", profile);
                        create.putExtra("FCMToken", FCM_token);
                        startActivity(create);
                    }
                }
            });
            reqQueue.add(jsonReq);
        }
    }

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
                address = geocoder.getFromLocation(Double.parseDouble(account.getJSONObject("location").getJSONObject("lat").getString("$numberDecimal")),
                        Double.parseDouble(account.getJSONObject("location").getJSONObject("lng").getString("$numberDecimal")), 1);
            } catch (IOException e) {
                Log.d(TAG, "failed to get location");
                e.printStackTrace();
            }
            String location = address.get(0).getLocality();
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
        } catch (JSONException e) {
            Log.d(TAG, "failed to parse json");
            e.printStackTrace();
        }
        return profile;
    }
}