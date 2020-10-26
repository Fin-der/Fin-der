package com.example.finder.Controller;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.finder.Chat.MessageAdapter;
import com.example.finder.Models.UserAccount;
import com.example.finder.Models.Message;
import com.example.finder.R;
import com.example.finder.Views.ChatView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatController {
    private Socket socket;
    private final String HOST_URL = "";
    private ChatView context;
    private UserAccount userAccount;
    private List<Message> messages;
    private RecyclerView msgRecycler;
    private MessageAdapter msgAdapter;

    public ChatController(ChatView context, UserAccount user) {
        try {
            this.userAccount = user;
            this.context = context;
            socket = IO.socket(HOST_URL);
            socket.connect();
            socket.emit("join", userAccount.getUserName());

            waitOnMessages();
        } catch (URISyntaxException e) {
            Log.e("SOCKET", "Failed to connect to Host");
            e.printStackTrace();
        }
    }

    private void initChatAdapters() {
        this.msgRecycler = context.findViewById(R.id.reyclerview_message_list);
        ArrayList<Message> temp = new ArrayList<>();
        UserAccount user = new UserAccount("Jacky", "15", "Male");
        this.msgAdapter = new MessageAdapter(context, temp, user);
        this.msgRecycler.setLayoutManager(new LinearLayoutManager(context));
        this.msgRecycler.setAdapter(msgAdapter);
    }

    private void waitOnMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket.on("message", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                    }
                });
            }
        }).start();
    }

    public boolean sendMessage(Message message) {
        return false;
    }
}
