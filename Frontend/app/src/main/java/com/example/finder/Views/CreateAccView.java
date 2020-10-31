package com.example.finder.Views;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.MainActivity;
import com.example.finder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.internal.Util;

public class CreateAccView extends AppCompatActivity {
    final static String TAG = "CreateAccView";

    private TextInputLayout username;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout phoneNumber;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;
    private TextInputLayout age;

    private TextInputEditText userEdit;
    private TextInputEditText firstNameEdit;
    private TextInputEditText lastNameEdit;
    private TextInputEditText ageEdit;
    private TextInputEditText emailEdit;
    private TextInputEditText phoneEdit;
    private TextInputEditText passwordEdit;
    private TextInputEditText confirmPasswordEdit;

    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private String url = "http://ec2-3-88-159-19.compute-1.amazonaws.com:3000/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        username = findViewById(R.id.editTextUsername);
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmailAddress);
        phoneNumber = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        age = findViewById(R.id.editTextAge);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);

        userEdit = findViewById(R.id.usernameInput);
        firstNameEdit = findViewById(R.id.firstNameInput);
        lastNameEdit = findViewById(R.id.lastNameInput);
        ageEdit = findViewById(R.id.ageInput);
        emailEdit = findViewById(R.id.emailInput);
        phoneEdit = findViewById(R.id.phoneInput);
        passwordEdit = findViewById(R.id.passwordInput);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordInput);

        userEdit.addTextChangedListener(new MyTextWatcher(userEdit));
        firstNameEdit.addTextChangedListener(new MyTextWatcher(firstNameEdit));
        lastNameEdit.addTextChangedListener(new MyTextWatcher(lastNameEdit));
        ageEdit.addTextChangedListener(new MyTextWatcher(ageEdit));
        emailEdit.addTextChangedListener(new MyTextWatcher(emailEdit));
//        phoneEdit.addTextChangedListener(new MyTextWatcher(phoneEdit));
        passwordEdit.addTextChangedListener(new MyTextWatcher(passwordEdit));
        confirmPasswordEdit.addTextChangedListener(new MyTextWatcher(confirmPasswordEdit));

        findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkTests()) {
                    JSONObject user = new JSONObject();
                    try {
                        //user.put("user_name", username.getText());
                        user.put("first_name", firstName.getEditText().getText().toString());
                        user.put("last_name", lastName.getEditText().getText().toString());
                        //user.put("age", age);
                        //user.put("email", email.getText().toString());
                        //user.put("phone", phoneNumber);
                        //user.put("password", password.getText().toString());
                        user.put("type", "type?");
                        Log.d(TAG, user.toString());
                    } catch (JSONException e) {
                        Log.d(TAG, "failed to create json");
                        e.printStackTrace();
                    }

                    reqQueue = Volley.newRequestQueue(CreateAccView.this);
                    jsonReq = new JsonObjectRequest(Request.Method.POST, url, user, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            Intent main = new Intent(CreateAccView.this, HomeView.class);
                            startActivity(main);
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

    private boolean checkUsername() {
        String usernameInput = username.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            username.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 20) {
            username.setError("Username too long");
            return false;
        } else {
            username.setError(null);
            return true;
        }
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
        } else if (!TextUtils.isEmpty(emailInput) && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Invalid email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean checkPassword() {
        String passwordInput = password.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean checkConfirmPassword() {
        String passwordInput = password.getEditText().getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();

        if (!confirmPasswordInput.equals(passwordInput)) {
            confirmPassword.setError("Must match password");
            return false;
        } else {
            confirmPassword.setError(null);
            return true;
        }
    }

    private boolean checkTests() {
        return !(!checkEmail() | !checkFirstName() | !checkLastName() | !checkAge() | !checkUsername() | !checkPassword() | !checkConfirmPassword());
    }

    private boolean confirmAccount() {
        if (!checkEmail() | !checkUsername() | !checkPassword() | !checkConfirmPassword()) {
            return false;
        }
        String input = "Username: " + username.getEditText().getText().toString();
        input += "\n";
        input += "Email: " + email.getEditText().getText().toString();
        input += "\n";
        input += "Password: " + password.getEditText().getText().toString();

        Toast.makeText(CreateAccView.this, input, Toast.LENGTH_SHORT).show();
        return true;
    }

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
                case R.id.usernameInput:
                    checkUsername();
                    break;
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
                case R.id.passwordInput:
                    checkPassword();
                    break;
                case R.id.confirmPasswordInput:
                    checkConfirmPassword();
                    break;
            }
        }
    }
}

