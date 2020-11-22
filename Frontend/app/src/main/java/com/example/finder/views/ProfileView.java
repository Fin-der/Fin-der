package com.example.finder.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.MainActivity;
import com.example.finder.models.UserAccount;
import com.example.finder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileView extends AppCompatActivity {
    private final static String TAG = "ProfileView";

    private TextView numMatches;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout age;
    private TextInputLayout email;
    //gender
    private TextInputLayout location;
    //gender preferred
    private TextInputLayout minAge;
    private TextInputLayout maxAge;
    private TextInputLayout proximity;
    //interest
    private TextInputLayout biography;

    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private GoogleSignInClient mGoogleSignInClient;

    private String url = HomeView.HOST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        TextView fullName = findViewById(R.id.fullNameText);
        firstName = findViewById(R.id.first_name_profile);
        lastName = findViewById(R.id.last_name_profile);
        age = findViewById(R.id.age_profile);
        email = findViewById(R.id.email_profile);
        numMatches = findViewById(R.id.number_matches);
        location = findViewById(R.id.location_profile);
        minAge = findViewById(R.id.min_age_profile);
        maxAge = findViewById(R.id.max_age_profile);
        proximity = findViewById(R.id.proximity_profile);
        biography = findViewById(R.id.bio_profile);

        final UserAccount user = (UserAccount) getIntent().getSerializableExtra("profile");

        String fullNameText = user.getFirstName() + " " + user.getLastName();
        fullName.setText(fullNameText);
        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        age.getEditText().setText(user.getAge());
        email.getEditText().setText(user.getEmail());
        numMatches.setText(user.getNumMatches());
        location.getEditText().setText(user.getLocation());
        minAge.getEditText().setText(user.getMinAge());
        maxAge.getEditText().setText(user.getMaxAge());
        proximity.getEditText().setText(user.getProximity());
        biography.getEditText().setText(user.getBiography());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
                alertDialogBuilder.setTitle("CONFIRM ACCOUNT DELETION");
                alertDialogBuilder.setMessage("Are you sure you want to delete your account?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // http request delete account
                        JSONObject idInfo = new JSONObject();
                        try {
                            idInfo.put("_id", user.getId());
                            Log.d(TAG, idInfo.toString());
                        } catch (JSONException e) {
                            Log.d(TAG, "failed to create json");
                            e.printStackTrace();
                        }
                        reqQueue = Volley.newRequestQueue(ProfileView.this);
                        jsonReq = new JsonObjectRequest(Request.Method.DELETE, url + user.getId(), idInfo, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                signOut();
                                Intent main = new Intent(ProfileView.this, MainActivity.class);
                                startActivity(main);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        reqQueue.add(jsonReq);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                empty since staying on the same page
                    }
                });
                alertDialogBuilder.create();
                alertDialogBuilder.show();
            }
        });

        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
                alertDialogBuilder.setTitle("CONFIRM ACCOUNT CHANGE");
                alertDialogBuilder.setMessage("Are you sure you want to update your account?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // http request delete account
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                empty since staying on the same page
                    }
                });
                alertDialogBuilder.create();
                alertDialogBuilder.show();
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.d(TAG, "Log out successful");
                        Toast.makeText(ProfileView.this, "Log out successful", Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(ProfileView.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                });
    }
}