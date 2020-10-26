package com.example.finder.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.MainActivity;
import com.example.finder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccView extends AppCompatActivity {
    final static String TAG = "CreateAccView";
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText age;
    private RequestQueue reqQueue;
    private JsonObjectRequest jsonReq;
    private String url = "http://192.168.1.84:3000/users";
    private boolean passed;

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

        findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject user = new JSONObject();
                try {
                    user.put("user_name", username.getText());
                    user.put("first_name", firstName.getText().toString());
                    user.put("last_name", lastName.getText().toString());
                    //user.put("age", age);
                    user.put("email", email.getText().toString());
                    //user.put("phone", phoneNumber);
                    user.put("password", password.getText().toString());
                    Log.d(TAG, user.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "failed to create json");
                    e.printStackTrace();
                }
                //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity);
                reqQueue = Volley.newRequestQueue(CreateAccView.this);
                jsonReq = new JsonObjectRequest(Request.Method.POST, url, user, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        passed = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                        passed = false;
                    }
                });
                reqQueue.add(jsonReq);
                passed = true;
                if (passed) {
                    Intent main = new Intent(CreateAccView.this, HomeView.class);
                    startActivity(main);
                } else {
                    Log.d(TAG, "failed");
                }
            }
        });
    }
}

