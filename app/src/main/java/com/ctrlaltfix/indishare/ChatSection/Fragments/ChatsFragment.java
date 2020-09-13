package com.ctrlaltfix.indishare.ChatSection.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ctrlaltfix.indishare.ChatSection.Adapters.ChatsViewAdapter;
import com.ctrlaltfix.indishare.ChatSection.ContactsListActivity;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FloatingActionButton fab;
    private DatabaseReference chatRef;
    private ArrayList<ChatsDetails> chatsDetails;
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRef = FirebaseDatabase.getInstance().getReference().child("chatsDetails").child(mUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true));
        fab = view.findViewById(R.id.fab);
        chatsDetails = new ArrayList<>();

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() != 0){
                    chatsDetails.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ChatsDetails model = dataSnapshot.getValue(ChatsDetails.class);
                        chatsDetails.add(model);
                    }
                    adapter = new ChatsViewAdapter(chatsDetails, getContext());
                    recyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(getActivity(), "No Chats Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(getActivity(), ContactsListActivity.class)
                );
            }
        });

        return view;
    }
}