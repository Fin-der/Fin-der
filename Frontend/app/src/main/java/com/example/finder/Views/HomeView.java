package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.finder.Chat.MessageBoardAdapter;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;

import java.util.ArrayList;

public class HomeView extends AppCompatActivity {
    private RecyclerView msgBoard;
    private MessageBoardAdapter msgBoardAdapter;
    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initButtons();
        initMessageBoard();
    }

    private void initButtons() {
        // adds more buttons & some way to get previous chats
        findViewById(R.id.toMsgs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMsgs = new Intent(HomeView.this, ChatView.class);
                startActivity(toMsgs);
            }
        });
    }

    private void initMessageBoard() {
        this.msgBoard = findViewById(R.id.home_MsgBoard);
        this.msgBoard.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<UserAccount> temp = new ArrayList<UserAccount>();
        temp.add(new UserAccount("Jacky", "0", "Male"));
        temp.add(new UserAccount("Nick", "1", "Female"));
        temp.add(new UserAccount("Cody", "2", "Female"));
        this.msgBoardAdapter = new MessageBoardAdapter(this, temp, user);
        this.msgBoard.setAdapter(this.msgBoardAdapter);
    }
}