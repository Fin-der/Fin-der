package com.example.finder.Controller;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.Chat.MessageAdapter;
import com.example.finder.Models.UserAccount;
import com.example.finder.Models.Message;
import com.example.finder.R;
import com.example.finder.Views.ChatView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatController {
    private Socket socket;
    private final String HOST_URL = "http://ec2-3-88-159-19.compute-1.amazonaws.com:3000/";
    private ChatView context;
    private UserAccount userAccount;
    private List<Message> messages;
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;
    private RequestQueue que;
    private String rId;
    private String roomId;

    public ChatController(ChatView context, UserAccount user, String rId) {
        this.userAccount = user;
        this.context = context;
        this.messages = new ArrayList<>();
        this.rId = rId;
        this.que = Volley.newRequestQueue(context);
        try {
            initChatRoom();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.socket = IO.socket(HOST_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initChatAdapters();
    }

    private void initChatAdapters() {
        this.msgRecycler = context.findViewById(R.id.reyclerview_message_list);
        this.msgAdapter = new MessageAdapter(context, this.messages, this.userAccount);
        this.msgRecycler.setLayoutManager(new LinearLayoutManager(context));
        this.msgRecycler.setAdapter(msgAdapter);
    }

    private void initChatRoom() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(userAccount.id);
        arr.put(rId);
        obj.put("userIds", arr);
        obj.put("type", "consumer-to-consumer");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, HOST_URL + "room/initiate", obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("REs", response.toString());
                    JSONObject room = (JSONObject) response.get("chatRoom");
                    roomId = (String) room.get("chatRoomId");
                    Log.d("ChatController", "Room Id: " + roomId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        this.que.add(req);

    }

    private void waitOnMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket.on("new message", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject) args[0];

                    }
                });
            }
        }).start();
    }

    public void sendMessage(Message message) {
        JSONObject data = new JSONObject();
        try {
            data.put("messageText", message.getMessage());
            data.put("userId", userAccount.id);
            data.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ChatController", "JSONobject to postMessage " + data.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    HOST_URL + "room/" + roomId + "/" + userAccount.id + "/message",
                        data,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ChatController", "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ChatController", "postMessage: " + error.toString());
                error.printStackTrace();
            }
        });

        this.que.add(req);

        this.messages.add(message);
        this.msgAdapter.notifyDataSetChanged();
    }
}
