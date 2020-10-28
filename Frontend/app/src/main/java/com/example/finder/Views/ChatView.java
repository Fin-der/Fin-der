package com.example.finder.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
    private final String GET_USERIDS = "http://ec2-3-88-159-19.compute-1.amazonaws.com:3000/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Intent intent = getIntent();
        this.receiver = intent.getStringExtra("chatterName");
        setTitle(this.receiver);
        this.user = (UserAccount) intent.getSerializableExtra("user");
        final RequestQueue que = Volley.newRequestQueue(this);
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, GET_USERIDS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arr = response.getJSONArray("users");
                    JSONObject user1 = (JSONObject) arr.get(1);
                    user.id = (String) user1.get("_id");
                    Log.d("hi", user.id);
                    JSONObject user2 = (JSONObject) arr.get(0);
                    rId = (String) user2.get("_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Chatview", "Could not get user ids, " + error.toString());
            }
        });

        que.add(req);

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
                Message msg = new Message("0", value, user.getUserName(), receiver, MessageAdapter.MSG_TYPE_SENT);
                controller.sendMessage(msg);
            }
        });
    }
}