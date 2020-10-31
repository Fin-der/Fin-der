package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.finder.Chat.MessageAdapter;
import com.example.finder.Chat.MessageBoardAdapter;
import com.example.finder.Controller.ChatController;
import com.example.finder.Models.UserAccount;
import com.example.finder.R;
import com.example.finder.Models.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        this.user.setId("9edf9faead43481190d75338144259c4");
        rId = "a388f144d6eb4573905d4829dee0bfde";
        //this.user.setId("a388f144d6eb4573905d4829dee0bfde");
        //rId = "9edf9faead43481190d75338144259c4" ;

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