package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import com.example.finder.Chat.MessageAdapter;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.example.finder.Models.Message;

import java.util.List;

public class ChatView extends AppCompatActivity {
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        this.msgRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        ArrayList<Message> temp = new ArrayList<>();
        Message msg1 = new Message("0", "A", "Cody", "Jacky", MessageAdapter.MSG_TYPE_RECEIVED);
        Message msg2 = new Message("0", "B", "Jacky", "Cody", MessageAdapter.MSG_TYPE_SENT);
        Message msg3 = new Message("0", "C", "Cody", "Jacky", MessageAdapter.MSG_TYPE_RECEIVED);
        UserAccount user = new UserAccount("Jacky", "15", UserAccount.Gender.MALE);
        temp.add(msg1);
        temp.add(msg2);
        temp.add(msg3);
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