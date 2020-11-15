package com.example.finder.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.finder.models.UserAccount;
import com.example.finder.R;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileView extends AppCompatActivity {
    private final static String TAG = "ProfileView";

    private TextView numMatches;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        TextView fullName = findViewById(R.id.fullNameText);
        firstName = findViewById(R.id.first_name_profile);
        lastName = findViewById(R.id.last_name_profile);
        TextInputLayout age = findViewById(R.id.age_profile);
        email = findViewById(R.id.email_profile);
        numMatches = findViewById(R.id.number_matches);

        UserAccount user = (UserAccount) getIntent().getSerializableExtra("profile");

        String fullNameText = user.getFirstName() + " " + user.getLastName();
        fullName.setText(fullNameText);
        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        age.getEditText().setText(user.getAge());
        email.getEditText().setText(user.getEmail());
        numMatches.setText(user.getNumMatches());

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = createAlertDialog("CONFIRM ACCOUNT DELETION", "Are you sure you want to delete your account?");
                alertDialog.show();
            }
        });

        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = createAlertDialog("CONFIRM ACCOUNT CHANGE", "Are you sure you want to update your account?");
                alertDialog.show();
            }
        });
    }

    private AlertDialog createAlertDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
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
        return alertDialogBuilder.create();
    }
}