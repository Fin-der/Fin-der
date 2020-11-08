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
    final static String TAG = "ProfileView";

    //private TextView numMatches;
    //private TextInputLayout firstName;
    //private TextInputLayout lastName;
    //private TextInputLayout email;
    //private TextInputLayout phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        TextView fullName = findViewById(R.id.fullNameText);
        //firstName = findViewById(R.id.first_name_profile);
        //lastName = findViewById(R.id.last_name_profile);
        TextInputLayout age = findViewById(R.id.age_profile);
        //email = findViewById(R.id.email_profile);
        //phoneNumber = findViewById(R.id.phone_number_profile);
        //numMatches = findViewById(R.id.number_matches);

        UserAccount user = (UserAccount) getIntent().getSerializableExtra("profile");

        fullName.setText(user.getUserName());
//        firstName.getEditText().setText();
//        lastName.getEditText().setText();
        age.getEditText().setText(user.getAge());
//        email.getEditText().setText();
//        phoneNumber.getEditText().setText();
//        numMatches.setText(user.getNumMatches);



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
            }
        });
        return alertDialogBuilder.create();
    }
}