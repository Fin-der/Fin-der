package com.example.finder.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Spinner genderSpinner1;
    private TextInputLayout location;
    //gender preferred
    private Spinner genderSpinner2;
    private TextInputLayout minAge;
    private TextInputLayout maxAge;
    private TextInputLayout proximity;
    //interest
    private Spinner interest1Spinner;
    private Spinner interest2Spinner;
    private Spinner interest3Spinner;
    private TextInputLayout biography;

    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private GoogleSignInClient mGoogleSignInClient;

    private String url = HomeView.HOST_URL;
    private UserAccount user;
    private String[] genderResult = new String[2];
    private String[] interestResult = new String[3];
    private int[] spinnerIndex = new int[5];

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

        user = (UserAccount) getIntent().getSerializableExtra("profile");

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

        genderSpinner1 = findViewById(R.id.genderSpinner1_profile);
        genderSpinner2 = findViewById(R.id.genderSpinner2_profile);
        interest1Spinner = findViewById(R.id.interest1Spinner_profile);
        interest2Spinner = findViewById(R.id.interest2Spinner_profile);
        interest3Spinner = findViewById(R.id.interest3Spinner_profile);

        getSpinnerIndex(user);

        spinnerSetup(genderSpinner1, R.array.gender_choices, genderResult, 0, spinnerIndex[0]);
        spinnerSetup(genderSpinner2, R.array.gender_choices2, genderResult, 1, spinnerIndex[1]);
        spinnerSetup(interest1Spinner, R.array.sport_choices, interestResult, 0, spinnerIndex[2]);
        spinnerSetup(interest2Spinner, R.array.food_choices, interestResult, 1, spinnerIndex[3]);
        spinnerSetup(interest3Spinner, R.array.hobby_choices, interestResult, 2, spinnerIndex[4]);

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = createDeleteAlertDialog();
                alertDialog.show();
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

    private void spinnerSetup(final Spinner spinner, int resource, final String[] output, final int outputIndex, int initialIndex) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(resource));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(initialIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                output[outputIndex] = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });
    }

    private void getSpinnerIndex(UserAccount user) {
        if (user.getGender().equals("Male")) {
            spinnerIndex[0] = 1;
        } else if (user.getGender().equals("Female")) {
            spinnerIndex[0] = 2;
        } else {
            spinnerIndex[0] = 3;
        }
        if (user.getPrefGender().equals("Male")) {
            spinnerIndex[1] = 1;
        } else if (user.getGender().equals("Female")) {
            spinnerIndex[1] = 2;
        } else if (user.getGender().equals("All")) {
            spinnerIndex[1] = 3;
        } else {
            spinnerIndex[1] = 4;
        }
        switch (user.getInterest()[0]) {
            case "Soccer":
                spinnerIndex[2] = 1;
                break;
            case "Football":
                spinnerIndex[2] = 2;
                break;
            case "Hockey":
                spinnerIndex[2] = 3;
                break;
            default:
                spinnerIndex[2] = 4;
                break;
        }
        switch (user.getInterest()[1]) {
            case "Pizza":
                spinnerIndex[3] = 1;
                break;
            case "Burger":
                spinnerIndex[3] = 2;
                break;
            case "Salad":
                spinnerIndex[3] = 3;
                break;
            default:
                spinnerIndex[3] = 4;
                break;
        }
        switch (user.getInterest()[2]) {
            case "Read":
                spinnerIndex[4] = 1;
                break;
            case "Cook":
                spinnerIndex[4] = 2;
                break;
            case "Watch Movies":
                spinnerIndex[4] = 3;
                break;
            default:
                spinnerIndex[4] = 4;
                break;
        }
    }

    private AlertDialog createDeleteAlertDialog() {
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
                jsonReq = new JsonObjectRequest(Request.Method.DELETE, url + "/users/" + user.getId(), idInfo, new Response.Listener<JSONObject>() {
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
        return alertDialogBuilder.create();
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