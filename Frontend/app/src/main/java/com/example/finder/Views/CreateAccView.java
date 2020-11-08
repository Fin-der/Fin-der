package com.example.finder.Views;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccView extends AppCompatActivity {
    private final static String TAG = "CreateAccView";

    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout phoneNumber;
    private TextInputLayout age;
    private TextInputLayout interest1;
    private TextInputLayout interest2;
    private TextInputLayout interest3;

    private Spinner genderSpinner;

    private UserAccount user;

    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private String url = "http://ec2-3-88-159-19.compute-1.amazonaws.com:3000/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmailAddress);
        phoneNumber = findViewById(R.id.editTextPhone);
        age = findViewById(R.id.editTextAge);
        interest1 = findViewById(R.id.editTextInterest1);
        interest2 = findViewById(R.id.editTextInterest2);
        interest3 = findViewById(R.id.editTextInterest3);

        TextInputEditText firstNameEdit = findViewById(R.id.firstNameInput);
        TextInputEditText lastNameEdit = findViewById(R.id.lastNameInput);
        TextInputEditText ageEdit = findViewById(R.id.ageInput);
        TextInputEditText emailEdit = findViewById(R.id.emailInput);
        TextInputEditText phoneEdit = findViewById(R.id.phoneInput);
        TextInputEditText interestEdit = findViewById(R.id.interest1Input);

        firstNameEdit.addTextChangedListener(new MyTextWatcher(firstNameEdit));
        lastNameEdit.addTextChangedListener(new MyTextWatcher(lastNameEdit));
        ageEdit.addTextChangedListener(new MyTextWatcher(ageEdit));
        emailEdit.addTextChangedListener(new MyTextWatcher(emailEdit));
        phoneEdit.addTextChangedListener(new MyTextWatcher(phoneEdit));
        interestEdit.addTextChangedListener(new MyTextWatcher(interestEdit));

        genderSpinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_choices));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        final String[] spinResult = new String[1];

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinResult[0] = genderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.user = (UserAccount) getIntent().getSerializableExtra("profile");

        findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkTests()) {
                    JSONObject userJson = new JSONObject();
                    try {
                        userJson.put("first_name", firstName.getEditText().getText().toString());
                        userJson.put("last_name", lastName.getEditText().getText().toString());
                        //userJson.put("age", age);
                        //userJson.put("email", email.getText().toString());
                        //userJson.put("phone", phoneNumber);
                        //userJson.put("gender", spinResult[0]);
                        //userJson.put("interest1", interest1.getEditText().getText().toString());
                        //userJson.put("interest2", interest2.getEditText().getText().toString());
                        //userJson.put("interest3", interest3.getEditText().getText().toString());
                        userJson.put("type", "type?");
                        Log.d(TAG, userJson.toString());
                    } catch (JSONException e) {
                        Log.d(TAG, "failed to create json");
                        e.printStackTrace();
                    }

                    reqQueue = Volley.newRequestQueue(CreateAccView.this);
                    jsonReq = new JsonObjectRequest(Request.Method.POST, url, userJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
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

    private boolean checkPhone() {
        String phoneInput = phoneNumber.getEditText().getText().toString().trim();

        if (phoneInput.isEmpty()) {
            phoneNumber.setError(null);
            return true;
        } else if (phoneInput.length() > 20) {
            phoneNumber.setError("Phone number too long");
            return false;
        } else if (!(!TextUtils.isEmpty(phoneInput) && Patterns.PHONE.matcher(phoneInput).matches())) {
            phoneNumber.setError("Invalid phone number");
            return false;
        } else {
            phoneNumber.setError(null);
            return true;
        }
    }

    private boolean checkInterest() {
        String interestInput = interest1.getEditText().getText().toString().trim();

        if (interestInput.isEmpty()) {
            interest1.setError("Must have at least 1 interest");
            return false;
        } else {
            interest1.setError(null);
            return true;
        }
    }

    private boolean checkTests() {
        return !(!checkEmail() | !checkFirstName() | !checkLastName() | !checkAge() | !checkPhone() | !checkInterest());
    }

//    private boolean confirmAccount() {
//        String input = "Email: " + email.getEditText().getText().toString();
//        input += "\n";
//
//        Toast.makeText(CreateAccView.this, input, Toast.LENGTH_SHORT).show();
//        return true;
//    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
                case R.id.phoneInput:
                    checkPhone();
                    break;
                case R.id.interest1Input:
                    checkInterest();
                    break;
                default:
                    break;
            }
        }
    }
}

