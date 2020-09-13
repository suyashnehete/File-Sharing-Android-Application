package com.ctrlaltfix.indishare.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ctrlaltfix.indishare.Adapters.ReceiveAnywhereAdapter;
import com.ctrlaltfix.indishare.Adapters.SendAnywhereAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.Models.ShareCodeModel;
import com.ctrlaltfix.indishare.Models.UserFileModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveFragment extends Fragment {

    public static RecyclerView recyclerView;
    public static EditText receiveCode;
    public static ArrayList<UserFileModel> userFileModels = new ArrayList<>();

    public ReceiveFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ReceiveFragment newInstance() {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        receiveCode = (EditText) view.findViewById(R.id.receiverCode);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        receiveCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                receiveData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void receiveData(final String code){
        FirebaseDatabase.getInstance().getReference().child("shareCodes").orderByKey().equalTo(code)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ShareCodeModel shareCodeModel = dataSnapshot.getValue(ShareCodeModel.class);
                                FirebaseDatabase.getInstance().getReference().child("Users").child(shareCodeModel.getUserId()).child(shareCodeModel.getFileId())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Log.d("suyash", code);
                                                if (snapshot.exists()){
                                                    boolean isExists = false;
                                                    UserFileModel userFileModel = snapshot.getValue(UserFileModel.class);
                                                    for (UserFileModel userFileModel1 : userFileModels){
                                                        if (userFileModel.getShareCode().equals(userFileModel1.getShareCode())){
                                                            isExists = true;
                                                        }
                                                    }
                                                    if (!isExists){
                                                        userFileModels.add(userFileModel);
                                                    }
                                                    recyclerView.setAdapter(new ReceiveAnywhereAdapter(userFileModels, getActivity()));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}