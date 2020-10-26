package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import com.example.finder.Chat.MessageAdapter;
import com.example.finder.Chat.MessageBoardAdapter;
import com.example.finder.Controller.ChatController;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.example.finder.Models.Message;

public class ChatView extends AppCompatActivity {
    private ChatController controller;
    private UserAccount user;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Intent intent = getIntent();
        this.receiver = intent.getStringExtra("chatterName");
        setTitle(this.receiver);
        this.user = (UserAccount) intent.getSerializableExtra("user");

        controller = new ChatController(this, user);

        init();
    }

    private void init() {
        findViewById(R.id.button_chatbox_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.edittext_chatbox);
                String value = input.getText().toString();
                input.getText().clear();
                Message msg = new Message("0", value, user.getUserName(), receiver, MessageAdapter.MSG_TYPE_SENT);
                controller.sendMessage(msg);
            }
        });
    }
}