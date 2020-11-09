package com.example.finder.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.R;
import com.example.finder.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    public static final int MSG_TYPE_SENT = 1;
    public static final int MSG_TYPE_RECEIVED = 2;

    private List<Message> messages;

    public MessageAdapter(List<Message> messageList) {
        this.messages = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == MSG_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_chat_msg, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == MSG_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_chat_msg, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case MSG_TYPE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case MSG_TYPE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            default:
                Log.d("MessageAdapter", "Failed to bind");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getMsgType();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView timeText;
        private TextView nameText;
        //private ImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            //profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getPostAt());
            nameText.setText(message.getSenderName());

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getPostAt());
        }
    }
}
