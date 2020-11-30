package com.example.finder.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finder.controller.ChatController;
import com.example.finder.models.UserAccount;
import com.example.finder.R;

/**
 * Activity view class for chatting functionality
 *
 */
public class ChatView extends AppCompatActivity {
    private ChatController controller;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Intent intent = getIntent();
        this.receiver = intent.getStringExtra("chatterName");
        setTitle(this.receiver);
        UserAccount user = (UserAccount) intent.getSerializableExtra("user");
        UserAccount friend = (UserAccount) intent.getSerializableExtra("friend");
        controller = new ChatController(this, user, friend);

        init();
    }

    /**
     * Initiates the send button so the ChatController will know when to send a message to backend
     *
     */
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