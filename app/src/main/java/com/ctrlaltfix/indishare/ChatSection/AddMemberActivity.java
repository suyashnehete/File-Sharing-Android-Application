package com.ctrlaltfix.indishare.ChatSection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.ChatSection.Adapters.AddMembersAdapter;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.ChatSection.Models.GroupMembers;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    ListView list;
    LinearLayout loading;

    ArrayList<UsersModel> usersModels;
    String id;

    public static ArrayList<String> mainArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        list = findViewById(R.id.list);
        loading = findViewById(R.id.loading);

        loading.setVisibility(View.GONE);

        mainArray = new ArrayList<>();

        id = getIntent().getStringExtra("id");
        usersModels = (ArrayList<UsersModel>) getIntent().getSerializableExtra("members");

        list.setAdapter(new AddMembersAdapter(Constant.registeredContactsList, this));

    }

    public void add(View view) {
        loading.setVisibility(View.VISIBLE);
        for (String userId : mainArray){
            FirebaseDatabase.getInstance().getReference("GroupMembers").child(id).child(userId)
                    .setValue(new GroupMembers(
                            userId,
                            id,
                            false
                    ));
            DatabaseReference chatDetails = FirebaseDatabase.getInstance().getReference("chatsDetails").child(userId);
            String chatsKey = chatDetails.push().getKey();
            chatDetails.child(chatsKey).setValue(
                    new ChatsDetails(
                            chatsKey,
                            id,
                            0,
                            true
                    )
            );
        }
        loading.setVisibility(View.GONE);
        onBackPressed();
        Toast.makeText(this, mainArray.size()+" Members Added", Toast.LENGTH_SHORT).show();
    }
}