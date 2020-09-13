package com.ctrlaltfix.indishare.ChatSection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    Button addMember;
    Button exitGroup;
    ListView list;
    Toolbar toolbar;
    ImageView icon;

    private String receiverName;
    private String receiverID;
    private String receiverUrl;
    private boolean isGroup;

    private ArrayList<UsersModel> usersModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        receiverName = getIntent().getStringExtra("receiverName");
        receiverID = getIntent().getStringExtra("receiverID");
        receiverUrl = getIntent().getStringExtra("receiverUrl");
        isGroup = getIntent().getBooleanExtra("isGroup", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(receiverName);

        addMember = findViewById(R.id.add_member);
        exitGroup = findViewById(R.id.exit_group);
        list = findViewById(R.id.list);
        icon = findViewById(R.id.icon);

        if (!TextUtils.isEmpty(receiverUrl)){
            Glide.with(this).load(receiverUrl).into(icon);
        }

        if (isGroup){
            usersModels = new ArrayList<>();
           addMember.setVisibility(View.VISIBLE);
           exitGroup.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("GroupMembers").child(receiverID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                usersModels.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    String id = snapshot1.getKey();
                                    FirebaseDatabase.getInstance().getReference("GroupMembers").child(receiverID).child(id)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        usersModels.add(snapshot.getValue(UsersModel.class));
                                                        list.setAdapter(new ArrayAdapter<>(UserProfileActivity.this, R.layout.support_simple_spinner_dropdown_item, usersModels));
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
        }else{
            addMember.setVisibility(View.GONE);
            exitGroup.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference().child("ChatUsers").child(receiverID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UsersModel model = snapshot.getValue(UsersModel.class);
                                list.setAdapter(new ArrayAdapter<String>(UserProfileActivity.this, R.layout.support_simple_spinner_dropdown_item, new String[]{model.getMobile()}));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(UserProfileActivity.this, AddMemberActivity.class)
                            .putExtra("id", receiverID)
                            .putExtra("members", usersModels)
                );
            }
        });

        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("GroupMembers").child(receiverID)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    startActivity(
                                            new Intent(UserProfileActivity.this, ChatHomeActivity.class)
                                    );
                                    Toast.makeText(UserProfileActivity.this, "Group Deleted", Toast.LENGTH_SHORT).show();
                                }else{

                                }
                            }
                        });
            }
        });
    }
}