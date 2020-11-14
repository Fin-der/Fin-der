package com.example.finder.views;

import android.content.Intent;
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
    private GoogleSignInClient mGoogleSignInClient;
    private final static String TAG = "HomeView";

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
        this.user.setMatches(new ArrayList<UserAccount>());
        initButtons();
        initMessageBoard();
        findMatches();
    }

    private void initButtons() {
        // adds more buttons & some way to get previous chats

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
                match.putExtra("User", user);
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

    private void initMessageBoard() {
        RecyclerView msgBoard = findViewById(R.id.home_MsgBoard);
        msgBoard.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<UserAccount> temp = new ArrayList<UserAccount>();
        temp.add(new UserAccount("Jacky", "0", "Male"));
        temp.add(new UserAccount("Nick", "1", "Female"));
        temp.add(new UserAccount("Cody", "2", "Female"));
        MessageBoardAdapter msgBoardAdapter = new MessageBoardAdapter(this, temp, user);
        msgBoard.setAdapter(msgBoardAdapter);
    }

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

    private void findMatches() {
        final String HOST_MATCH = "http://192.168.1.72:3000/match/";
        RequestQueue que = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, HOST_MATCH + 0, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray matches = (JSONArray) response.get("matches");
                            Log.d("HomeView", "Matches" + matches.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HomeView", "Could not find matches");
                user.setMatches(new ArrayList<UserAccount>());
            }
        });
        que.add(req);
    }
}