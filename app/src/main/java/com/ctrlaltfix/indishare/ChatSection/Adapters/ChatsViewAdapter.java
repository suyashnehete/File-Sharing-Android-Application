package com.ctrlaltfix.indishare.ChatSection.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.MessageActivity;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.ChatSection.Models.GroupInfo;
import com.ctrlaltfix.indishare.ChatSection.Models.MessageModel;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.UI.CircularImageView;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.ctrlaltfix.indishare.Utils.Constant.phoneNumbers;


public class ChatsViewAdapter extends RecyclerView.Adapter<ChatsViewAdapter.ViewHolder> {

    private final ArrayList<ChatsDetails> mValues;
    private final Context context;
    private final FirebaseUser mUser;

    public ChatsViewAdapter(ArrayList<ChatsDetails> items, Context context) {
        mValues = items;
        this.context = context;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatsDetails chatsDetails = mValues.get(position);
        final String[] receiverName = new String[1];
        final String[] receiverUrl = new String[1];
        receiverName[0] = null;
        if (!chatsDetails.isGroup()) {
            FirebaseDatabase.getInstance().getReference().child("ChatUsers").child(chatsDetails.getUserId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                                for (ContactList contactList : Constant.registeredContactsList){
                                    if (contactList.getMobile().equals(usersModel.getMobile())){
                                        receiverName[0] = contactList.getName();
                                        break;
                                    }
                                }
                                if (receiverName[0] == null){
                                    receiverName[0] = usersModel.getName() != null ? usersModel.getName() : usersModel.getMobile();
                                }
                                receiverUrl[0] = usersModel.getUrl() == null ? "" : usersModel.getUrl();
                                holder.userName.setText(receiverName[0]);
                                if (!TextUtils.isEmpty(receiverUrl[0])) {
                                    Glide.with(context).load(receiverUrl[0]).into(holder.userProfile);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            FirebaseDatabase.getInstance().getReference("Groups").child(chatsDetails.getUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);
                                receiverName[0] = groupInfo.getName();
                                receiverUrl[0] = groupInfo.getUrl() == null ? "" : groupInfo.getUrl();
                                holder.userName.setText(receiverName[0]);
                                if (!TextUtils.isEmpty(receiverUrl[0])) {
                                    Glide.with(context).load(receiverUrl[0]).into(holder.userProfile);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        FirebaseDatabase.getInstance().getReference().child("ChatMessages").child(chatsDetails.getChatId()).orderByKey().limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getChildrenCount() != 0 && snapshot.getValue() != null) {
                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            holder.lastMessage.setText(messageModel.getMessage());
                            holder.lastMessageTime.setText(messageModel.getDate());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    context.startActivity(
                            new Intent(context, MessageActivity.class)
                                    .putExtra("receiverName", receiverName[0])
                                    .putExtra("receiverID", chatsDetails.getUserId())
                                    .putExtra("receiverUrl", receiverUrl[0])
                                    .putExtra("isGroup", chatsDetails.isGroup())
                    );
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircularImageView userProfile;
        public final TextView userName;
        public final TextView lastMessageTime;
        public final TextView lastMessage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            userName = view.findViewById(R.id.userName);
            userProfile = view.findViewById(R.id.userProfile);
            lastMessageTime = view.findViewById(R.id.lastMessageTime);
            lastMessage = view.findViewById(R.id.lastMessage);
        }

    }
}