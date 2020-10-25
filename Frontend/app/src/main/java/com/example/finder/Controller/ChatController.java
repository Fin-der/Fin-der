package com.example.finder.Controller;

import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.finder.Models.UserAccount;
import com.example.finder.Models.Message;

import java.net.URISyntaxException;

public class ChatController {
    private Socket socket;
    private final String HOST_URL = "";
    private UserAccount userAccount;

    public ChatController() {
        try {
            socket = IO.socket(HOST_URL);
            socket.connect();
            socket.emit("join", userAccount.getUserName());

            waitOnMessages();
        } catch (URISyntaxException e) {
            Log.e("SOCKET", "Failed to connect to Host");
            e.printStackTrace();
        }
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
}
