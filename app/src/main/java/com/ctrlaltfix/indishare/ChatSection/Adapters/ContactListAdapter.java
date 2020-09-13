package com.ctrlaltfix.indishare.ChatSection.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.MessageActivity;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.ChatSection.Models.MessageModel;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.UI.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private final ArrayList<ContactList> mValues;
    private final Context context;

    public ContactListAdapter(ArrayList<ContactList> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new ContactListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactListAdapter.ViewHolder holder, int position) {
        final ContactList contactList = mValues.get(position);
        if(contactList.getProfile()!=null && !contactList.getProfile().isEmpty()) Glide.with(context).load(contactList.getProfile()).into(holder.userProfile);
        holder.userName.setText(contactList.getName());
        holder.userNumber.setText(contactList.getMobile());
        if (contactList.getId() != null){
            holder.invite.setVisibility(View.GONE);
        }else{
            holder.invite.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        new Intent(context, MessageActivity.class)
                                .putExtra("receiverName", contactList.getName())
                                .putExtra("receiverID", contactList.getId())
                                .putExtra("receiverUrl", contactList.getProfile()==null ? "" : contactList.getProfile())
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
        public final TextView userNumber;
        public final TextView invite;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            userName = view.findViewById(R.id.userName);
            userProfile = view.findViewById(R.id.userProfile);
            userNumber = view.findViewById(R.id.userNumber);
            invite = view.findViewById(R.id.invite);
        }

    }
}