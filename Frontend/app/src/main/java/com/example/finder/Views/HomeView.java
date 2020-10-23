package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.finder.R;

public class HomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    private void init() {
        // adds more buttons & some way to get previous chats
        findViewById(R.id.toMsgs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMsgs = new Intent(HomeView.this, ChatView.class);
                startActivity(toMsgs);
            }
        });
    }
}