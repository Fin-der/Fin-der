package com.example.finder.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.example.finder.controller.ImageLoaderHelper;
import com.example.finder.models.UserAccount;
import com.example.finder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileView extends AppCompatActivity {
    /*Tag for checking Logcat*/
    private final static String TAG = "ProfileView";

    /*Text input variables*/
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout age;
    private TextInputLayout email;
    private TextInputLayout location;
    private TextInputLayout minAge;
    private TextInputLayout maxAge;
    private TextInputLayout proximity;
    private TextInputLayout biography;

    private GoogleSignInClient mGoogleSignInClient;

    private String url = HomeView.HOST_URL;
    private UserAccount user;

    /*Temp variables to transfer to json and set initial spinner values*/
    private String[] genderResult = new String[2];
    private String[] interestResult = new String[3];
    private int[] spinnerIndex = new int[5];
    private double[] longLat = {200, 100};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        /*Define element id in view for each view-only element*/
        TextView fullName = findViewById(R.id.fullNameText);
        TextView numFriends = findViewById(R.id.number_matches);
        ImageView profilePic = findViewById(R.id.profile_image);

        /*Define element id in view for each text input variables*/
        firstName = findViewById(R.id.first_name_profile);
        lastName = findViewById(R.id.last_name_profile);
        age = findViewById(R.id.age_profile);
        email = findViewById(R.id.email_profile);
        location = findViewById(R.id.location_profile);
        minAge = findViewById(R.id.min_age_profile);
        maxAge = findViewById(R.id.max_age_profile);
        proximity = findViewById(R.id.proximity_profile);
        biography = findViewById(R.id.bio_profile);

        /*Obtain data passed through intents*/
        user = (UserAccount) getIntent().getSerializableExtra("profile");

        /*Set the text input user information in the profile view*/
        String fullNameText = user.getFirstName() + " " + user.getLastName();
        fullName.setText(fullNameText);
        numFriends.setText(user.getNumFriends());
        ImageLoaderHelper.loadProfilePic(this, profilePic, user.getpfpUrl(), 100, 100);

        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        age.getEditText().setText(user.getAge());
        email.getEditText().setText(user.getEmail());
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

        /*Define the element id in view for each drop down box*/
        Spinner genderSpinner1 = findViewById(R.id.genderSpinner1_profile);
        Spinner genderSpinner2 = findViewById(R.id.genderSpinner2_profile);
        Spinner interest1Spinner = findViewById(R.id.interest1Spinner_profile);
        Spinner interest2Spinner = findViewById(R.id.interest2Spinner_profile);
        Spinner interest3Spinner = findViewById(R.id.interest3Spinner_profile);

        /*Get the initial drop down box choice index for initialization*/
        getGenderSpinnerIndex(user);
        getInterestSpinnerIndex(user);

        /*Setup the spinner to the correct choice based on the user profile*/
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
                AlertDialog alertDialog = createUpdateAlertDialog();
                alertDialog.show();
            }
        });
    }

    /**
     * This function initializes the adapter and initial values to line up with the user profile
     * @param spinner the spinner variable being setup
     * @param resource the res string array id
     * @param output the array to set the chosen option to
     * @param outputIndex the position in the output array to write to
     * @param initialIndex the initial position to set the spinner to
     */
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

    /**
     * This function finds the initial position of the gender spinners
     * @param user the current user account
     */
    private void getGenderSpinnerIndex(UserAccount user) {
        if (user.getGender().equals("Male")) {
            spinnerIndex[0] = 1;
        } else if (user.getGender().equals("Female")) {
            spinnerIndex[0] = 2;
        } else {
            spinnerIndex[0] = 3;
        }
        switch (user.getPrefGender()) {
            case "Male":
                spinnerIndex[1] = 1;
                break;
            case "Female":
                spinnerIndex[1] = 2;
                break;
            case "All":
                spinnerIndex[1] = 3;
                break;
            default:
                spinnerIndex[1] = 4;
                break;
        }
    }

    /**
     * This function finds the initial position of the interest spinners
     * @param user the current user account
     */
    private void getInterestSpinnerIndex(UserAccount user) {
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

    /**
     * This function creates an alert dialog with yes or no buttons for deleting the current account
     * Contains a volley request to delete the account if yes
     * @return the alert dialog to be shown
     */
    private AlertDialog createDeleteAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
        alertDialogBuilder.setTitle("CONFIRM ACCOUNT DELETION");
        alertDialogBuilder.setMessage("Are you sure you want to delete your account?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                JSONObject idInfo = new JSONObject();           // json containing the user id
                try {
                    idInfo.put("_id", user.getId());
                    Log.d(TAG, idInfo.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "failed to create json");
                    e.printStackTrace();
                }
                RequestQueue reqQueue = Volley.newRequestQueue(ProfileView.this);
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE, url + "/users/" + user.getId(), idInfo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        signOut();
                        Intent main = new Intent(ProfileView.this, MainActivity.class);
                        startActivity(main);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileView.this, "Server Error", Toast.LENGTH_SHORT).show();
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

    /**
     * This function creates an alert dialog with yes or no buttons for updating the current account
     * Contains a volley request to update the account information if yes
     * @return the alert dialog to be shown
     */
    private AlertDialog createUpdateAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
        alertDialogBuilder.setTitle("CONFIRM ACCOUNT CHANGE");
        alertDialogBuilder.setMessage("Are you sure you want to update your account?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Geocoder geocoder = new Geocoder(ProfileView.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(user.getLocation(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "something wrong finding location");
                }
                if (list.size() > 0) {
                    Address address = list.get(0);
                    longLat[0] = address.getLongitude();
                    longLat[1] = address.getLatitude();
                }
                JSONObject updateJson = packJson(); // json of the updated user account
                RequestQueue reqQueue = Volley.newRequestQueue(ProfileView.this);
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT, url + "/users/" + user.getId(), updateJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent home = new Intent(ProfileView.this, HomeView.class);
                        packUserAccount();          // updated user account
                        home.putExtra("profile", user);
                        Toast.makeText(ProfileView.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        startActivity(home);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileView.this, "Server Error", Toast.LENGTH_SHORT).show();
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

    /**
     * This function packs user account information from text inputs
     * and stores it in the existing UserAccount model
     */
    private void packUserAccount() {
        user.setFirstName(firstName.getEditText().getText().toString());
        user.setLastName(lastName.getEditText().getText().toString());
        user.setAge(Integer.parseInt(age.getEditText().getText().toString()));
        user.setGender(genderResult[0]);
        user.setEmail(email.getEditText().getText().toString());
        user.setLocation(location.getEditText().getText().toString());
        user.setPrefGender(genderResult[1]);
        if (minAge.getEditText().getText().toString().trim().isEmpty()) {
            user.setMinAge(Integer.parseInt(age.getEditText().getText().toString()) - 2);
        } else {
            user.setMinAge(Integer.parseInt(minAge.getEditText().getText().toString()));
        }
        if (maxAge.getEditText().getText().toString().trim().isEmpty()) {
            user.setMaxAge(Integer.parseInt(age.getEditText().getText().toString()) + 2);
        } else {
            user.setMaxAge(Integer.parseInt(maxAge.getEditText().getText().toString()));
        }
        if (proximity.getEditText().getText().toString().trim().isEmpty()) {
            user.setProximity(15);
        } else {
            user.setProximity(Integer.parseInt(proximity.getEditText().getText().toString()));
        }
        user.setInterest(interestResult);
        user.setBiography(biography.getEditText().getText().toString());
    }

    /**
     * This function packs the user account information from text inputs into a JSONObject
     * @return a JSONObject containing the user account information
     */
    private JSONObject packJson() {
        JSONArray interests = new JSONArray();
        interests.put(interestResult[0]);
        interests.put(interestResult[1]);
        interests.put(interestResult[2]);
        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("lng", longLat[0]);
            locationJson.put("lat", longLat[1]);
        } catch (JSONException e) {
            Log.d(TAG, "failed to create location json");
            e.printStackTrace();
        }
        JSONObject ageRangeJson = new JSONObject();
        try {
            if (minAge.getEditText().getText().toString().trim().isEmpty()) {
                ageRangeJson.put("min", Integer.parseInt(age.getEditText().getText().toString()) - 2);
            } else {
                ageRangeJson.put("min", Integer.parseInt(minAge.getEditText().getText().toString()));
            }
            if (maxAge.getEditText().getText().toString().trim().isEmpty()) {
                ageRangeJson.put("max", Integer.parseInt(age.getEditText().getText().toString()) + 2);
            } else {
                ageRangeJson.put("max", Integer.parseInt(maxAge.getEditText().getText().toString()));
            }
        } catch (JSONException e) {
            Log.d(TAG, "failed to create age range json");
            e.printStackTrace();
        }
        JSONObject preferenceJson = new JSONObject();
        try {
            preferenceJson.put("gender", genderResult[1]);
            preferenceJson.put("ageRange", ageRangeJson);
            if (proximity.getEditText().getText().toString().trim().isEmpty()) {
                preferenceJson.put("proximity", 15);
            } else {
                preferenceJson.put("proximity", Integer.parseInt(proximity.getEditText().getText().toString()));
            }
        } catch (JSONException e) {
            Log.d(TAG, "failed to create preference json");
            e.printStackTrace();
        }
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("_id", user.getId());
            userJson.put("firstName", firstName.getEditText().getText().toString());
            userJson.put("lastName", lastName.getEditText().getText().toString());
            userJson.put("age", age.getEditText().getText().toString());
            userJson.put("gender", genderResult[0]);
            userJson.put("email", email.getEditText().getText().toString());
            userJson.put("geoLocation", locationJson);
            userJson.put("preferences", preferenceJson);
            userJson.put("interests", interests);
            userJson.put("description", biography.getEditText().getText().toString());
            userJson.put("profileURL", user.getpfpUrl());
            Log.d(TAG, userJson.toString());
        } catch (JSONException e) {
            Log.d(TAG, "failed to create user json");
            e.printStackTrace();
        }
        Log.d(TAG, userJson.toString());
        return userJson;
    }

    /**
     * This function signs out the current user
     */
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