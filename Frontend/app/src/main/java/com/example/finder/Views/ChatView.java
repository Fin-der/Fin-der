package com.example.finder.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finder.Controller.ChatController;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;

public class ChatView extends AppCompatActivity {
    private ChatController controller;
    private UserAccount user;
    private String receiver;
    private String rId;
    private final String GET_USERIDS = "http://192.168.1.72:3000/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        this.receiver = intent.getStringExtra("chatterName");
        setTitle(this.receiver);
        this.user = (UserAccount) intent.getSerializableExtra("user");

        this.user.setId("2430507c1a7d41f09f003d8c4dc6d442");
        rId = "753b1955dcef440fa21197092f659b38";
        //this.user.setId("753b1955dcef440fa21197092f659b38");
        //rId = "2430507c1a7d41f09f003d8c4dc6d442" ;

        controller = new ChatController(this, user, rId);

        init();
    }

    private void init() {
        findViewById(R.id.button_chatbox_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.edittext_chatbox);
                String value = input.getText().toString();
                input.getText().clear();
                controller.sendMessage(value);
            }
        });
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public void onBackPressed() {
        controller.cleanUp();
        Log.d("ChatView", "Closed Socket");
        finish();
    }

}