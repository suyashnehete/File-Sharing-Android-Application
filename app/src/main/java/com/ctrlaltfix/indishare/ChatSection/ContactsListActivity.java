package com.ctrlaltfix.indishare.ChatSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ctrlaltfix.indishare.ChatSection.Adapters.ContactListAdapter;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ContactsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout loading;
    private ArrayList<String> firebaseNumbers = new ArrayList<>();
    private ArrayList<String> firebaseNames = new ArrayList<>();
    private ArrayList<String> firebaseProfiles = new ArrayList<>();
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private ArrayList<String> phoneNames = new ArrayList<>();

    Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        recyclerView = findViewById(R.id.list);
        loading = findViewById(R.id.loading);
            loading.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ContactListAdapter(Constant.registeredContactsList, ContactsListActivity.this));
        loading.setVisibility(View.GONE);
    }
}

