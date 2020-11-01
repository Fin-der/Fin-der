package com.example.finder.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.finder.R;

public class ProfileView extends AppCompatActivity {
    final static String TAG = "ProfileView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

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