package com.example.finder.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

public class ProfileView extends AppCompatActivity {
    final static String TAG = "ProfileView";

    private TextView fullName;
    private TextView numMatches;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout age;
    private TextInputLayout email;
    private TextInputLayout phoneNumber;
    private TextInputLayout password;

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        fullName = findViewById(R.id.fullNameText);
        firstName = findViewById(R.id.first_name_profile);
        lastName = findViewById(R.id.last_name_profile);
        numMatches = findViewById(R.id.number_matches);

        this.user = (UserAccount) getIntent().getSerializableExtra("profile");

        fullName.setText(user.getUserName());
        firstName.getEditText().setText(user.getUserName());
        lastName.getEditText().setText(user.getUserName());


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileView.this);
        alertDialogBuilder.setTitle("CONFIRM ACCOUNT DELETION");
        alertDialogBuilder.setMessage("Are you sure youwant to delete your account?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // http request delete account
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}