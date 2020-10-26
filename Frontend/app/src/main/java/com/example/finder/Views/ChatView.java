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
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.example.finder.Models.Message;

public class ChatView extends AppCompatActivity {
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("chatterName"));

        this.msgRecycler = findViewById(R.id.reyclerview_message_list);
        ArrayList<Message> temp = new ArrayList<>();
        UserAccount user = new UserAccount("Jacky", "15", "Male");
        this.msgAdapter = new MessageAdapter(this, temp, user);
        this.msgRecycler.setLayoutManager(new LinearLayoutManager(this));
        this.msgRecycler.setAdapter(msgAdapter);
        init();
    }

    private void init() {
        findViewById(R.id.button_chatbox_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.edittext_chatbox);
                String value = input.getText().toString();

            }
        });
    }
}