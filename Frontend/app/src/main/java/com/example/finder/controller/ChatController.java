package com.example.finder.controller;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.example.finder.chat.MessageAdapter;
import com.example.finder.models.UserAccount;
import com.example.finder.models.Message;
import com.example.finder.R;
import com.example.finder.views.ChatView;
import com.example.finder.views.HomeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Controller for the ChatView
 * Sends and receives messages to/from backend and controls MessageAdapter
 * Connects to backend via socket and Volley REST calls
 *
 */
public class ChatController {
    private Socket socket;
    private final String HOST_URL = HomeView.HOST_URL;
    private final ChatView context;
    private final UserAccount userAccount;
    private final List<Message> messages;
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;
    private final RequestQueue que;
    private final String rId;
    private final UserAccount friend;
    private String roomId;
    // Position in the conversation used to ensure the correct range of past messages will be received
    private int chatPos;
    // Semaphore to help manage multiple calls for retrieving past messages from the backend
    private final Semaphore queLock = new Semaphore(0);

    public ChatController(ChatView context, UserAccount user, UserAccount friend) {
        this.userAccount = user;
        this.context = context;
        this.messages = new ArrayList<>();
        this.rId = friend.getId();
        this.friend = friend;
        this.que = Volley.newRequestQueue(context);
        this.chatPos = 0;
        try {
            initChatRoom();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initChatAdapters();
    }

    /**
     * Initiates the chatAdapters and sets screen to bottom of the screen
     * Will grab and load old messages from the backend if those exist
     *
     */
    private void initChatAdapters() {
        this.msgRecycler = context.findViewById(R.id.reyclerview_message_list);
        this.msgAdapter = new MessageAdapter(this.messages, this.friend);
        this.msgRecycler.setLayoutManager(new LinearLayoutManager(context));
        this.msgRecycler.setAdapter(msgAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (roomId == null)
                    Thread.yield();
                JsonObjectRequest request = grabConversation();
                que.add(request);
            }
        }).start();
        /*
         * Move screen back to bottom when typing a message
         */
        msgRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, final int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    msgRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msgRecycler.smoothScrollToPosition(bottom);
                        }
                    }, 100);
                }
            }
        });
        /*
         * Retrieves older messages if ChatView has reached the top RecyclerView
         * Uses queLock to ensure only 1 request will happen at a time, so chatPos will be
         * incremented correctly and only the required messages will be receieved
         */
        msgRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                queLock.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d("ChatController", "At top, must get more messages!");
                            que.add(grabConversation());
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * Creates a "room" and generates roomId in the backend if it does not already exist,
     * otherwise, retrieve roomId from backend
     *
     * @throws JSONException if error parsing response message
     */
    private void initChatRoom() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(userAccount.getId());
        arr.put(rId);
        obj.put("userIds", arr);
        obj.put("type", "consumer-to-consumer");
        Log.d("ChatController", "Initiate: " + obj.toString());
        final JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST, HOST_URL + "/room/initiate", obj,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("ChatController", response.toString());
                    JSONObject room = (JSONObject) response.get("chatRoom");
                    roomId = (String) room.get("chatRoomId");
                    Log.d("ChatController", "Room Id: " + roomId);
                    waitOnMessages();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ChatController", error.toString());
            }
        });
        que.add(req);
    }

    /**
     * Generates the JsonObjectRequest or the Req.body for the getRecentConversationByRoomId API
     * Call
     *
     * @return Request body for getRecentConversation call to retrieve old messages
     */
    private JsonObjectRequest grabConversation() {
        JSONObject skip = new JSONObject();
        try {
            skip.put("skip", chatPos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ChatController", "chatPos @getRecentConversation " + chatPos);
        return new JsonObjectRequest(Request.Method.GET,
                HOST_URL + "/room/" + roomId + "/" + chatPos, skip,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray convo = response.getJSONArray("conversation");
                            ArrayList<Message> list = new ArrayList<>();
                            for (int i = 0; i < convo.length(); i++) {
                                Message msg = parseMessage((JSONObject) convo.get(i));
                                list.add(msg);
                            }
                            Collections.reverse(list);
                            chatPos += list.size();
                            Log.d("ChatController", "Messages Size: " + messages.size());
                            int oldSize = messages.size();
                            /*
                             * Add older messages to the current messages list and notify adapter
                             *
                             * If this is the first call to retrieve the conversation, will also
                             * scroll to the bottom of the screen for the user
                             */
                            messages.addAll(0, list);
                            msgAdapter.notifyItemRangeInserted(0, list.size());
                            if (oldSize == 0 && !messages.isEmpty())
                                msgRecycler.getLayoutManager().scrollToPosition(messages.size() - 1);
                            queLock.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queLock.release();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ChatController", "Cant get messages");
                queLock.release();
            }
        });
    }

    /**
     * Sets up the connection socket for message retrieval from the backend
     *
     */
    private void waitOnMessages() {
        try {
            this.socket = IO.socket(HOST_URL);
            /*
             * This handles messages sent by the friend and notifies the adapter
             */
            socket.on("new message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    final JSONObject response = (JSONObject) args[0];
                    Log.d("ChatController", "From socket: " + response.toString());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg = parseMessage(response.getJSONObject("message"));
                                chatPos++;
                                messages.add(msg);
                                msgAdapter.notifyDataSetChanged();
                                msgRecycler.getLayoutManager().scrollToPosition(messages.size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
            socket.connect();
            socket.emit("identity", userAccount.getId());
            socket.emit("subscribe", roomId);
        } catch (URISyntaxException e) {
            Log.e("ChatController", "Fail to Create Socket");
            this.socket.close();
            e.printStackTrace();
        }
    }

    /**
     * Parses the JSON formatted message sent by their friend/contact
     *
     * @param message JSONObject containing information about the received message and sender
     * @return Message Model containing information about the received message and sender
     * @throws JSONException if error parsing through message JSONObject
     */
    private Message parseMessage(JSONObject message) throws JSONException {
        String messageText = ((JSONObject) message.get("message")).getString("messageText");
        String userId = message.getString("postedByUser");
        String postAt = message.getString("createdAt");
        Message msg;
        if (userId.equals(rId)) {
            msg = new Message(message.getString("_id"),
                    messageText, rId, ((ChatView) context).getReceiver(), MessageAdapter.MSG_TYPE_RECEIVED, postAt);
        } else {
            msg = new Message(message.getString("_id"),
                    messageText, userAccount.getId(), userAccount.getFirstName(), MessageAdapter.MSG_TYPE_SENT, postAt);
        }
        return msg;
    }

    /**
     * Sends user's message to backend
     * Don't need to add message to messages list here, will be added by socket handler
     *
     * @param message User's message to their friend
     */
    public void sendMessage(String message) {
        JSONObject data = new JSONObject();
        try {
            data.put("messageText", message);
            data.put("userId", userAccount.getId());
            data.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ChatController", "JSONobject to postMessage " + data.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    HOST_URL + "/room/" + roomId + "/" + userAccount.getId() + "/message",
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
    }

    /**
     * Ensure socket has been disconnected properly
     *
     */
    public void cleanUp() {
        this.socket.disconnect();
    }
}
