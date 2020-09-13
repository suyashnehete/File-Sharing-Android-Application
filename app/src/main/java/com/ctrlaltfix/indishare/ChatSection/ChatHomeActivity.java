package com.ctrlaltfix.indishare.ChatSection;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.ChatSection.Models.GroupInfo;
import com.ctrlaltfix.indishare.ChatSection.Models.GroupMembers;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.ctrlaltfix.indishare.ChatSection.Adapters.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.ctrlaltfix.indishare.Utils.Constant.firebaseNames;
import static com.ctrlaltfix.indishare.Utils.Constant.firebaseNumbers;
import static com.ctrlaltfix.indishare.Utils.Constant.phoneNames;
import static com.ctrlaltfix.indishare.Utils.Constant.phoneNumbers;

public class ChatHomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabs;
    LinearLayout loading;

    Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        loading = findViewById(R.id.loading);

        if (Constant.registeredContactsList.size() == 0) {
            loading.setVisibility(View.VISIBLE);
            new Thread(new load(this)).start();
        }else{
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(sectionsPagerAdapter);
            tabs.setupWithViewPager(viewPager);
            loading.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_group:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Create Group");
                final EditText editText = new EditText(this);
                editText.setHint("Enter Group Name");
                builder.setView(editText);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createGroup(editText.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", null);
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
                    builder.show();
                }
                break;
        }
        return true;
    }

    private void createGroup(String groupName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        final String key = reference.push().getKey();
        if (TextUtils.isEmpty(groupName)){
            Toast.makeText(this, "Group Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
        }else{
            reference.child(key).setValue(
                    new GroupInfo(
                            key,
                            groupName,
                            null
                    )
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseDatabase.getInstance().getReference("GroupMembers").child(key).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new GroupMembers(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    key,
                                    true
                            ));
                    DatabaseReference chatDetails = FirebaseDatabase.getInstance().getReference("chatsDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String chatsKey = chatDetails.push().getKey();
                    chatDetails.child(chatsKey).setValue(
                            new ChatsDetails(
                                    chatsKey,
                                    key,
                                    0,
                                    true
                            )
                    );
                    Toast.makeText(ChatHomeActivity.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatHomeActivity.this, "Group Creation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class load implements Runnable{
        Context context;
        public load(Context context) {
            this.context = context;
        }
        @Override
        public void run() {
            FirebaseDatabase.getInstance().getReference().child("ChatUsers")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                UsersModel model = dataSnapshot.getValue(UsersModel.class);
                                firebaseNames.add(model.getId());
                                firebaseNumbers.add(model.getMobile());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            Constant.registeredContactsList.clear();
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    final String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    final String profile = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "= ?", new String[]{name}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = phone.replaceAll(" ", "");
                            phone = phone.replaceAll("-", "");
                            final String finalPhone = phone;
                            phoneNames.add(name);
                            phoneNumbers.add(finalPhone);
                        }
                        pCur.close();
                    }
                }
                if (cur != null) {
                    cur.close();
                }
                for (String phone : phoneNumbers){
                    if (phone.length() > 9 && !(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().contains(phone) || FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(phone))){
                        for (String firebase : firebaseNumbers) {
                            if (phone.contains(firebase)) {
                                add(phone, phoneNumbers.indexOf(phone));
                                break;
                            } else if (firebase.contains(phone)) {
                                add(firebase, phoneNumbers.indexOf(phone));
                                break;
                            } else if (phone.equals(firebase)) {
                                add(phone, phoneNumbers.indexOf(phone));
                                break;
                            }
                        }
                    }
                }

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(context, getSupportFragmentManager());
                        viewPager.setAdapter(sectionsPagerAdapter);
                        tabs.setupWithViewPager(viewPager);
                        loading.setVisibility(View.GONE);
                    }
                });
            }
        }

        public void add(String phone, final int index){
            FirebaseDatabase.getInstance().getReference().child("ChatUsers").child(firebaseNames.get(firebaseNumbers.indexOf(phone)))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UsersModel model = snapshot.getValue(UsersModel.class);
                                boolean isAvailable = false;
                                if (Constant.registeredContactsList.size() == 0) {
                                    Constant.registeredContactsList.add(new ContactList(model.getMobile(), phoneNames.get(index), model.getId(), model.getUrl() == null ? "" : model.getUrl()));
                                }
                                for (int i = 0; i < Constant.registeredContactsList.size(); i++) {
                                    if (Constant.registeredContactsList.get(i).getMobile().equals(model.getMobile())) {
                                        isAvailable = true;
                                        break;
                                    }
                                }
                                if (!isAvailable) {
                                    Constant.registeredContactsList.add(new ContactList(model.getMobile(), phoneNames.get(index), model.getId(), model.getUrl() == null ? "" : model.getUrl()));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}