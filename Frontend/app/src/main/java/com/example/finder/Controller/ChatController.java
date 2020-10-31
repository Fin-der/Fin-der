package com.example.finder.Controller;

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
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatController {
    private Socket socket;
    private String HOST_URL = "http://192.168.1.72:3000/";
    private ChatView context;
    private UserAccount userAccount;
    private List<Message> messages;
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;
    private RequestQueue que;
    private String rId;
    private String roomId;
    private int chatPos;
    private Semaphore queLock = new Semaphore(0);

    public ChatController(ChatView context, UserAccount user, String rId) {
        this.userAccount = user;
        this.context = context;
        this.messages = new ArrayList<>();
        this.rId = rId;
        this.que = Volley.newRequestQueue(context);
        this.chatPos = 0;
        try {
            initChatRoom();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initChatAdapters();
    }

    private void initChatAdapters() {
        this.msgRecycler = context.findViewById(R.id.reyclerview_message_list);
        this.msgAdapter = new MessageAdapter(context, this.messages, this.userAccount);
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
        msgRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
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

    private void initChatRoom() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(userAccount.getId());
        arr.put(rId);
        obj.put("userIds", arr);
        obj.put("type", "consumer-to-consumer");
        Log.d("ChatController", "Initiate: " + obj.toString());
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, HOST_URL + "room/initiate", obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("REs", response.toString());
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
            public void onErrorResponse(VolleyError error) {}
        });
        que.add(req);
    }

    private JsonObjectRequest grabConversation() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                HOST_URL + "room/" + roomId + "/" + chatPos, null,
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
                            chatPos += list.size();
                            Log.d("ChatController", "Messages Size: " + messages.size());
                            int oldSize = messages.size();
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
        return request;
    }

    private void waitOnMessages() {
        try {
            this.socket = IO.socket(HOST_URL);
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

    private Message parseMessage(JSONObject message) throws JSONException {
        String messageText = ((JSONObject) message.get("message")).getString("messageText");
        String userId = ((JSONObject) message.get("postedByUser")).getString("_id");
        Message msg;
        if (userId.equals(rId)) {
            msg = new Message(message.getString("_id"),
                    messageText, rId, ((ChatView) context).getReceiver(), MessageAdapter.MSG_TYPE_RECEIVED);
        } else {
            msg = new Message(message.getString("_id"),
                    messageText, userAccount.getId(), userAccount.getUserName(), MessageAdapter.MSG_TYPE_SENT);
        }
        return msg;
    }

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
                    HOST_URL + "room/" + roomId + "/" + userAccount.getId() + "/message",
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

    public void cleanUp() {
        this.socket.disconnect();
    }
}
