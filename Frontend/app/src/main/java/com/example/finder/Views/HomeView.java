package com.example.finder.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.finder.Chat.MessageBoardAdapter;
import com.example.finder.MainActivity;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class HomeView extends AppCompatActivity {
    private RecyclerView msgBoard;
    private MessageBoardAdapter msgBoardAdapter;
    private UserAccount user;
    //private GoogleSignInClient mGoogleSignInClient;
    private Button signOutButton;
    final static String TAG = "HomeView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.user = new UserAccount("Nick", "0", "Male");
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

        findViewById(R.id.home_profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent profile = new Intent(HomeView.this, );
                //startActivity(profile);
            }
        });

        findViewById(R.id.home_FindMatchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent match = new Intent(HomeView.this, );
                //startActivity(match);
            }
        });

//        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//                Intent main = new Intent(HomeView.this, MainActivity.class);
//                startActivity(main);
//            }
//        });
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

//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                        Log.d(TAG, "Log out successful");
//
//                    }
//                });
//    }
}