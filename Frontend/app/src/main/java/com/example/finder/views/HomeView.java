package com.example.finder.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.MainActivity;
import com.example.finder.R;
import com.example.finder.chat.MessageBoardAdapter;
import com.example.finder.models.UserAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeView extends AppCompatActivity {
    private UserAccount user;
    private final ArrayList<UserAccount> toBeMatched = new ArrayList<>();
    private GoogleSignInClient mGoogleSignInClient;
    private final static String TAG = "HomeView";
    public static String id;
    public final static String HOST_URL = "http://ec2-3-88-159-19.compute-1.amazonaws.com:3000";
    public final static String MATCH_LIMIT = "&limit=25";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        this.user = (UserAccount) getIntent().getSerializableExtra("profile");
        this.user.setMatches(toBeMatched);
        initButtons();
    }

    /**
     * Retrieves friends for the message board
     * Important if new friends were made during absence
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        user.getMatches().clear();
        if (user.getFriendMatches() == null)
            user.setFriendMatches(new ArrayList<UserAccount>());
        else
            user.getFriendMatches().clear();
        findMatches();
        getFriends();
    }

    /**
     * Initializes buttons for HomeView
     *
     */
    private void initButtons() {
        findViewById(R.id.home_profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(HomeView.this, ProfileView.class);
                profile.putExtra("profile", user);
                startActivity(profile);
            }
        });

        findViewById(R.id.home_FindMatchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent match = new Intent(HomeView.this, MatchView.class);
                match.putExtra("profile", user);
                match.putExtra("matches", toBeMatched);
                startActivity(match);
            }
        });

        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent main = new Intent(HomeView.this, MainActivity.class);
                startActivity(main);
            }
        });
    }

    /**
     * Initializes message board of contants/friends
     *
     */
    private void initMessageBoard() {
        RecyclerView msgBoard = findViewById(R.id.home_MsgBoard);
        msgBoard.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<UserAccount> temp = new ArrayList<UserAccount>();
        if (user.getFriendMatches() == null) {
            user.setFriendMatches(temp);
            Log.e("HomeView", "friendMatches was null");
        }
        MessageBoardAdapter msgBoardAdapter =
                                new MessageBoardAdapter(this, user.getFriendMatches(), user);
        msgBoard.setAdapter(msgBoardAdapter);
    }

    /**
     * Make backend call to get information of user's friends
     *
     */
    private void getFriends() {
        final String url = HomeView.HOST_URL + "/match/friend/";
        RequestQueue que = Volley.newRequestQueue(this);
        user.setFriendMatches(new ArrayList<UserAccount>());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                url + user.getId(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray friends = (JSONArray) response.get("friends");
                    for (int i = 0; i < friends.length(); i++) {
                        JSONObject acc = friends.getJSONObject(i).getJSONObject("to");
                        String id = acc.getString("_id");
                        String firstName = acc.getString("firstName");
                        String lastName = acc.getString("lastName");
                        Uri profileURI = Uri.parse(acc.getString("profileURL"));
                        UserAccount friend = new UserAccount(id, firstName, lastName);
                        friend.setpfpUrl(profileURI);
                        user.getFriendMatches().add(friend);
                    }
                    initMessageBoard();
                } catch (JSONException e) {
                    Log.d(TAG, "failed to parse friend json");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
            }
        });
        que.add(jsonReq);
    }

    /**
     * Handles user sign out
     *
     */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.d(TAG, "Log out successful");
                        Toast.makeText(HomeView.this, "Log out successful", Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(HomeView.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                });
    }

    /**
     * Makes backend call to get potential matches for user
     *
     */
    private void findMatches() {
        final String HOST_MATCH = HOST_URL + "/match/" + user.getId() + "/?page=0" + HomeView.MATCH_LIMIT;
        RequestQueue que = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, HOST_MATCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try { //String id, String firstName, String lastName, String email) {
                            MatchView.parseMatches(response, toBeMatched);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HomeView", "Could not find matches");
            }
        });
        que.add(req);
    }
}