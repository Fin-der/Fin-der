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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateAccView extends AppCompatActivity {
    private final static String TAG = "CreateAccView";

    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout age;
    private TextInputLayout location;
    private TextInputLayout minAge;
    private TextInputLayout maxAge;
    private TextInputLayout proximity;
    private TextInputLayout biography;

    private Spinner genderSpinner1;
    private Spinner genderSpinner2;
    private Spinner interest1Spinner;
    private Spinner interest2Spinner;
    private Spinner interest3Spinner;

    private UserAccount user;

    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private String url = HomeView.HOST_URL + "/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmailAddress);
        age = findViewById(R.id.editTextAge);
        location = findViewById(R.id.editTextLocation);
        minAge = findViewById(R.id.editTextAgeMin);
        maxAge = findViewById(R.id.editTextAgeMax);
        proximity = findViewById(R.id.editTextProximity);
        biography = findViewById(R.id.editTextBio);

        TextInputEditText firstNameEdit = findViewById(R.id.firstNameInput);
        TextInputEditText lastNameEdit = findViewById(R.id.lastNameInput);
        TextInputEditText ageEdit = findViewById(R.id.ageInput);
        TextInputEditText emailEdit = findViewById(R.id.emailInput);
        final TextInputEditText locationEdit = findViewById(R.id.locationInput);

        firstNameEdit.addTextChangedListener(new MyTextWatcher(firstNameEdit));
        lastNameEdit.addTextChangedListener(new MyTextWatcher(lastNameEdit));
        ageEdit.addTextChangedListener(new MyTextWatcher(ageEdit));
        emailEdit.addTextChangedListener(new MyTextWatcher(emailEdit));
        locationEdit.addTextChangedListener(new MyTextWatcher(locationEdit));

        final double[] longLat = {0, 0};

        locationEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String searchString = locationEdit.getText().toString();
                    Geocoder geocoder = new Geocoder(CreateAccView.this);
                    List<Address> list = new ArrayList<>();
                    try {
                        list = geocoder.getFromLocationName(searchString, 1);
                    } catch (IOException e) {
                        // catch
                    }
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        // transfer lat long to user scheme
                        longLat[0] = address.getLongitude();
                        longLat[1] = address.getLatitude();
                    }
                }
                return false;
            }
        });

        genderSpinner1 = findViewById(R.id.genderSpinner1);
        genderSpinner2 = findViewById(R.id.genderSpinner2);
        interest1Spinner = findViewById(R.id.interest1Spinner);
        interest2Spinner = findViewById(R.id.interest2Spinner);
        interest3Spinner = findViewById(R.id.interest3Spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_choices));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner1.setAdapter(adapter);

        final String[] genderResult = new String[2];

        genderSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderResult[0] = genderSpinner1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });

        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_choices2));
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner2.setAdapter(adapter5);

        genderSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderResult[1] = genderSpinner2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sport_choices));
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest1Spinner.setAdapter(adapter2);

        final String[] interestResult = new String[3];

        interest1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestResult[0] = interest1Spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.food_choices));
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest2Spinner.setAdapter(adapter3);

        interest2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestResult[1] = interest2Spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });

        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.hobby_choices));
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest3Spinner.setAdapter(adapter4);

        interest3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestResult[2] = interest3Spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                nothing action handled in the error spinner error checkers
            }
        });

        this.user = (UserAccount) getIntent().getSerializableExtra("profile");

        // auto fill information
        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        email.getEditText().setText(user.getEmail());

        findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO move geolocate here
                if (checkTests()) {
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
                        userJson.put("first_name", firstName.getEditText().getText().toString());
                        userJson.put("last_name", lastName.getEditText().getText().toString());
//                        userJson.put("age", age.getEditText().getText().toString());
//                        userJson.put("gender", genderResult[0]);
//                        userJson.put("email", email.getEditText().getText().toString());
//                        userJson.put("location", locationJson);
//                        userJson.put("preferences", preferenceJson);
//                        userJson.put("interests", interestResult);
//                        userJson.put("description", biography.getEditText().getText().toString());
                        Log.d(TAG, userJson.toString());
                    } catch (JSONException e) {
                            Log.d(TAG, "failed to create user json");
                            e.printStackTrace();
                    }
                    Log.d(TAG, userJson.toString());
                    reqQueue = Volley.newRequestQueue(CreateAccView.this);
                    jsonReq = new JsonObjectRequest(Request.Method.POST, url, userJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            //TODO: update the user account information when creating account
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
                            Intent home = new Intent(CreateAccView.this, HomeView.class);
                            home.putExtra("profile", user);
                            startActivity(home);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    reqQueue.add(jsonReq);
                }
            }
        });
    }

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

    private boolean checkMinAge() {
        String minInput = minAge.getEditText().getText().toString().trim();
        String maxInput = maxAge.getEditText().getText().toString().trim();

        if (minInput.isEmpty()) {
            return true;
        } else if (Integer.parseInt(minInput) > 150) {
            minAge.setError("Invalid age");
            return false;
        } else if (Integer.parseInt(minInput) > Integer.parseInt(maxInput)) {
            minAge.setError("Minimum age must be less than or equal to Maximum age");
            return false;
        } else {
            minAge.setError(null);
            return true;
        }
    }

    private boolean checkMaxAge() {
        String minInput = minAge.getEditText().getText().toString().trim();
        String maxInput = maxAge.getEditText().getText().toString().trim();

        if (maxInput.isEmpty()) {
            return true;
        } else if (Integer.parseInt(minInput) > 150) {
            minAge.setError("Invalid age");
            return false;
        } else if (Integer.parseInt(maxInput) < Integer.parseInt(minInput)) {
            minAge.setError("Maximum age must be greater than or equal to Minimum age");
            return false;
        } else {
            minAge.setError(null);
            return true;
        }
    }

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

    private boolean checkTests() {
        return !(!checkEmail() | !checkFirstName() | !checkLastName() | !checkAge() | !checkLocation() | !spinnerChecks() | !checkMinAge() | !checkMaxAge());
    }

    private boolean spinnerChecks() {
        return (checkSpinner(genderSpinner1) | checkSpinner(genderSpinner2) | checkSpinner(interest1Spinner) | checkSpinner(interest2Spinner) | checkSpinner(interest3Spinner));
    }

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
                default:
                    break;
            }
        }
    }
}

