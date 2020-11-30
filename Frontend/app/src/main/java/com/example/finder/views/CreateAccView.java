package com.example.finder.views;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.models.UserAccount;
import com.example.finder.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateAccView extends AppCompatActivity {
    /*Tag for checking Logcat*/
    private final static String TAG = "CreateAccView";

    /*Text input variables*/
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout age;
    private TextInputLayout location;
    private TextInputLayout minAge;
    private TextInputLayout maxAge;
    private TextInputLayout proximity;
    private TextInputLayout biography;

    /*Drop down box spinner variables*/
    private Spinner genderSpinner1;
    private Spinner genderSpinner2;
    private Spinner interest1Spinner;
    private Spinner interest2Spinner;
    private Spinner interest3Spinner;

    private UserAccount user;
    private String FCM_token;

    /*Volley request variables*/
    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private String url = HomeView.HOST_URL + "/users/";

    /*Temp variables to transfer to json*/
    private double[] longLat = {200, 100};
    private String[] genderResult = new String[2];
    private String[] interestResult = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        /*Define element id in view for each text input variable*/
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmailAddress);
        age = findViewById(R.id.editTextAge);
        location = findViewById(R.id.editTextLocation);
        minAge = findViewById(R.id.editTextAgeMin);
        maxAge = findViewById(R.id.editTextAgeMax);
        proximity = findViewById(R.id.editTextProximity);
        biography = findViewById(R.id.editTextBio);

        /*Edit text variables are defined to add an input listener to each*/
        TextInputEditText firstNameEdit = findViewById(R.id.firstNameInput);
        TextInputEditText lastNameEdit = findViewById(R.id.lastNameInput);
        TextInputEditText ageEdit = findViewById(R.id.ageInput);
        TextInputEditText emailEdit = findViewById(R.id.emailInput);
        TextInputEditText locationEdit = findViewById(R.id.locationInput);
        TextInputEditText minAgeEdit = findViewById(R.id.minAgeInput);
        TextInputEditText maxAgeEdit = findViewById(R.id.maxAgeInput);
        TextInputEditText bioEdit = findViewById(R.id.bioInput);

        /*Edit text variables are given an input listener to do error checking*/
        firstNameEdit.addTextChangedListener(new MyTextWatcher(firstNameEdit));
        lastNameEdit.addTextChangedListener(new MyTextWatcher(lastNameEdit));
        ageEdit.addTextChangedListener(new MyTextWatcher(ageEdit));
        emailEdit.addTextChangedListener(new MyTextWatcher(emailEdit));
        locationEdit.addTextChangedListener(new MyTextWatcher(locationEdit));
        minAgeEdit.addTextChangedListener(new MyTextWatcher(minAgeEdit));
        maxAgeEdit.addTextChangedListener(new MyTextWatcher(maxAgeEdit));
        bioEdit.addTextChangedListener(new MyTextWatcher(bioEdit));

        /*Define element view id for each drop down box variable*/
        genderSpinner1 = findViewById(R.id.genderSpinner1);
        genderSpinner2 = findViewById(R.id.genderSpinner2);
        interest1Spinner = findViewById(R.id.interest1Spinner);
        interest2Spinner = findViewById(R.id.interest2Spinner);
        interest3Spinner = findViewById(R.id.interest3Spinner);

        /*initialize drop down boxes*/
        spinnerSetup(genderSpinner1, R.array.gender_choices, genderResult, 0);
        spinnerSetup(genderSpinner2, R.array.gender_choices2, genderResult, 1);
        spinnerSetup(interest1Spinner, R.array.sport_choices, interestResult, 0);
        spinnerSetup(interest2Spinner, R.array.food_choices, interestResult, 1);
        spinnerSetup(interest3Spinner, R.array.hobby_choices, interestResult, 2);

        /*Obtain data passed through intents*/
        this.user = (UserAccount) getIntent().getSerializableExtra("profile");
        this.FCM_token = (String) getIntent().getSerializableExtra("FCMToken");

        /*auto-fill information from google account*/
        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        email.getEditText().setText(user.getEmail());

        findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Based on the location inputted, Geocoder gets the longitude and latitude if the location is real*/
                Geocoder geocoder = new Geocoder(CreateAccView.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(location.getEditText().getText().toString(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "something wrong finding location");
                }
                if (list.size() > 0) {
                    Address address = list.get(0);
                    longLat[0] = address.getLongitude();
                    longLat[1] = address.getLatitude();
                }
                if (checkTests()) {
                    JSONObject userJson = packJson();
                    reqQueue = Volley.newRequestQueue(CreateAccView.this);
                    jsonReq = new JsonObjectRequest(Request.Method.POST, url, userJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            packUserAccount();
                            Intent home = new Intent(CreateAccView.this, HomeView.class);
                            home.putExtra("profile", user); // pass the UserAccount into the home intent
                            Toast.makeText(CreateAccView.this, "Account Creation Successful", Toast.LENGTH_SHORT).show();
                            startActivity(home);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Error: " + error.getMessage());
                            error.printStackTrace();
                            Toast.makeText(CreateAccView.this, "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    reqQueue.add(jsonReq);
                } else {
                    Toast.makeText(CreateAccView.this, "Something is Wrong with your Information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This function initializes the adapter and the choices for each drop down box
     * @param spinner the spinner variable being setup
     * @param resource the res string array id
     * @param output the array to set the chosen option to
     * @param arrayIndex the position in the output array to write to
     */
    private void spinnerSetup(final Spinner spinner, int resource, final String[] output, final int arrayIndex) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(resource));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                output[arrayIndex] = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });
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
            userJson.put("FCMToken", this.FCM_token);
            userJson.put("profileURL", user.getpfpUrl());
            Log.d(TAG, userJson.toString());
        } catch (JSONException e) {
            Log.d(TAG, "failed to create user json");
            e.printStackTrace();
        }
        return userJson;
    }

    /**
     * This function checks whether the first name text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkFirstName() {
        String firstNameInput = firstName.getEditText().getText().toString().trim();

        if (firstNameInput.isEmpty()) {
            firstName.setError("Field can't be empty");
            return false;
        } else if (firstNameInput.length() > 20) {
            firstName.setError("First name too long");
            return false;
        } else {
            firstName.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the last name text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkLastName() {
        String lastNameInput = lastName.getEditText().getText().toString().trim();

        if (lastNameInput.isEmpty()) {
            lastName.setError("Field can't be empty");
            return false;
        } else if (lastNameInput.length() > 20) {
            lastName.setError("Last name too long");
            return false;
        } else {
            lastName.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the age text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkAge() {
        String ageInput = age.getEditText().getText().toString().trim();

        if (ageInput.isEmpty()) {
            age.setError("Field can't be empty");
            return false;
        } else if (Integer.parseInt(ageInput) > 150) {
            age.setError("Invalid age");
            return false;
        } else {
            age.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the minimum age text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkMinAge() {
        String minInput = minAge.getEditText().getText().toString().trim();

        if (minInput.isEmpty()) {
            minAge.setError(null);
            return true;
        } else if (Integer.parseInt(minInput) > 150) {
            minAge.setError("Invalid age");
            return false;
        } else {
            minAge.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the maximum age text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkMaxAge() {
        String maxInput = maxAge.getEditText().getText().toString().trim();

        if (maxInput.isEmpty()) {
            maxAge.setError(null);
            return true;
        } else if (Integer.parseInt(maxInput) > 150) {
            maxAge.setError("Invalid age");
            return false;
        } else {
            maxAge.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the maximum age is greater than or equal to the minimum age
     * @return true if valid, false otherwise
     */
    private boolean checkAgeDiff() {
        String minInput = minAge.getEditText().getText().toString().trim();
        String maxInput = maxAge.getEditText().getText().toString().trim();

        if (minInput.isEmpty() || maxInput.isEmpty()) {
            minAge.setError(null);
            maxAge.setError(null);
            return true;
        } else if (Integer.parseInt(maxInput) < Integer.parseInt(minInput)) {
            minAge.setError("Minimum age must be less than or equal to Maximum age");
            maxAge.setError("Maximum age must be greater than or equal to Minimum age");
            return false;
        } else {
            minAge.setError(null);
            maxAge.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the email text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkEmail() {
        String emailInput = email.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else if (!(!TextUtils.isEmpty(emailInput) && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())) {
            email.setError("Invalid email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the location text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkLocation() {
        String locationInput = location.getEditText().getText().toString().trim();

        if (locationInput.isEmpty()) {
            location.setError("Field can't be empty");
            return false;
        } else {
            location.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the location inputted is a real location based on the longitude and latitude
     * @param longitude longitude of the location inputted
     * @param latitude latitude of the location inputted
     * @return true if valid, false otherwise
     */
    private boolean isLocationValid(double longitude, double latitude) {
        String locationInput = location.getEditText().getText().toString().trim();

        if (locationInput.isEmpty()) {
            location.setError("Field can't be empty");
            return false;
        } else if (longitude == 200 | latitude == 100) {
            location.setError("Invalid location");
            return false;
        } else {
            location.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether the biography text input is valid
     * @return true if valid, false otherwise
     */
    private boolean checkBio() {
        String bioInput = biography.getEditText().getText().toString().trim();

        if (bioInput.isEmpty()) {
            biography.setError(null);
            return true;
        } else if (bioInput.length() > 150) {
            biography.setError("Biography too long");
            return false;
        } else {
            biography.setError(null);
            return true;
        }
    }

    /**
     * This function checks whether a spinner input is chosen
     * @param spinner the drop down box being checked
     * @return true if valid, false otherwise
     */
    private boolean checkSpinner(Spinner spinner) {
        String spinnerInput = spinner.getSelectedItem().toString();

        if (spinnerInput.equals("Select Gender") || spinnerInput.equals("Select Favourite Sport") || spinnerInput.equals("Select Favourite Food") || spinnerInput.equals("Select Hobby")) {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setTextColor(Color.RED);
            return false;
        } else {
            return true;
        }
    }

    /**
     * This function checks all the tests
     * @return  true if valid, false otherwise
     */
    private boolean checkTests() {
        return !(!checkEmail() | !checkFirstName() | !checkLastName() | !checkAge() | !checkLocation() | spinnerChecks()
                | !checkMinAge() | !checkMaxAge() | !isLocationValid(longLat[0], longLat[1]) | !checkAgeDiff() | !checkBio());
    }

    /**
     * This function checks all the spinner tests
     * @return  true if valid, false otherwise
     */
    private boolean spinnerChecks() {
        return (!checkSpinner(genderSpinner1) | !checkSpinner(genderSpinner2) | !checkSpinner(interest1Spinner)
                | !checkSpinner(interest2Spinner) | !checkSpinner(interest3Spinner));
    }

    /**
     * This is a class defined to do checks on the input of text (real-time)
     */
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            this is meant to be empty since not checking before text is changed
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            this is meant to be empty since not checking when text is changed
        }

        /*After a character is input into a text edit, this does a check for the respective text input*/
        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.firstNameInput:
                    checkFirstName();
                    break;
                case R.id.lastNameInput:
                    checkLastName();
                    break;
                case R.id.ageInput:
                    checkAge();
                    break;
                case R.id.emailInput:
                    checkEmail();
                    break;
                case R.id.locationInput:
                    checkLocation();
                    break;
                case R.id.minAgeInput:
                    checkMinAge();
                    break;
                case R.id.maxAgeInput:
                    checkMaxAge();
                    break;
                case R.id.bioInput:
                    checkBio();
                    break;
                default:
                    break;
            }
        }
    }
}

