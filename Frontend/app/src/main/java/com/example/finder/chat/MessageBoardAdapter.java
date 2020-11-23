package com.example.finder.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.models.UserAccount;
import com.example.finder.R;
import com.example.finder.views.ChatView;

import java.util.List;

public class MessageBoardAdapter extends RecyclerView.Adapter {
    private List<UserAccount> contacts;
    private Context context;
    private UserAccount user;

    public MessageBoardAdapter(Context context, List<UserAccount> contacts, UserAccount user) {
        this.context = context;
        this.contacts = contacts;
        this.user = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msgboard_profile, parent, false);
        return new MessageBoardContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserAccount user = contacts.get(position);
        ((MessageBoardContact) holder).bind(user, this.user);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    private class MessageBoardContact extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView profilePic;
        private View itemView;

        public MessageBoardContact(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.profilePic = itemView.findViewById(R.id.msgboard_profilepic);
            this.name = itemView.findViewById(R.id.msgboard_profilename);
        }

        private void bind(final UserAccount friend, final UserAccount user) {
            final String fullFriendName = friend.getFirstName() + " " + friend.getLastName();
            this.name.setText(fullFriendName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toMsgs = new Intent(context,ChatView.class);
                    toMsgs.putExtra("chatterName", fullFriendName);
                    toMsgs.putExtra("user", user);
                    toMsgs.putExtra("rId", friend.getId());
                    context.startActivity(toMsgs);
                }
            });
        }
    }
}
