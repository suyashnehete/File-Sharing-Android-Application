package com.ctrlaltfix.indishare.ChatSection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.Adapters.MessageViewAdapter;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.MessageModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private final static int PICK_IMAGE = 101;
    private ListView list;
    private EditText textSend;
    private ImageButton sendBtn;
    private ImageButton sendDoc;
    private ImageView toolbarImage;
    private ImageView thumbnail;
    private TextView toolbarName;
    private AppBarLayout appBarLayout;
    private DatabaseReference databaseReference;
    private DatabaseReference root;
    private StorageReference storageReference;
    private FirebaseUser mUser;
    private String receiverName;
    private String receiverID;
    private String receiverUrl;
    private boolean isGroup;
    private ArrayList<MessageModel> chats;
    private Uri uri = null;
    private CardView card;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        list = findViewById(R.id.list);
        textSend = findViewById(R.id.send_text);
        sendBtn = findViewById(R.id.send_btn);
        sendDoc = findViewById(R.id.send_doc);
        toolbarImage = findViewById(R.id.toolbarImage);
        toolbarName = findViewById(R.id.toolbarName);
        card = findViewById(R.id.card);
        thumbnail = findViewById(R.id.thumbnail);
        appBarLayout = findViewById(R.id.bar);
        chats = new ArrayList<>();

        card.setVisibility(View.GONE);
        thumbnail.setVisibility(View.GONE);

        receiverName = getIntent().getStringExtra("receiverName");
        receiverID = getIntent().getStringExtra("receiverID");
        receiverUrl = getIntent().getStringExtra("receiverUrl");
        isGroup = getIntent().getBooleanExtra("isGroup", false);

        toolbarName.setText(receiverName);
        if (!TextUtils.isEmpty(receiverUrl)){
            Glide.with(this).load(receiverUrl).into(toolbarImage);
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (isGroup){
            registerChats(receiverID);
        }else {
            FirebaseDatabase.getInstance().getReference("ChatMessages").child(receiverID + mUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() != 0) {
                                registerChats(receiverID + mUser.getUid());
                            } else {
                                registerChats(mUser.getUid() + receiverID);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.GONE);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                card.setVisibility(View.GONE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.GONE);
                send();
            }
        });

        sendDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card.getVisibility() == View.VISIBLE){
                    card.setVisibility(View.GONE);
                }else{
                    card.setVisibility(View.VISIBLE);
                }
            }
        });

        appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(MessageActivity.this, UserProfileActivity.class)
                            .putExtra("isGroup", isGroup)
                            .putExtra("receiverID", receiverID)
                            .putExtra("receiverName", receiverName)
                            .putExtra("receiverUrl", receiverUrl)
                );
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });
    }

    private void registerChats(final String id){
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatMessages").child(id);
        storageReference = FirebaseStorage.getInstance().getReference("ChatMessages").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getChildrenCount() != 0){
                    chats.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MessageModel model = dataSnapshot.getValue(MessageModel.class);
                        chats.add(model);
                    }

                    list.setAdapter(new MessageViewAdapter(chats, MessageActivity.this, databaseReference, storageReference, isGroup));
                    list.setSelection(list.getAdapter().getCount()-1);
                    FirebaseDatabase.getInstance().getReference().child("chatsDetails").child(mUser.getUid())
                            .child(id).setValue(
                            new ChatsDetails(
                                    id,
                                    receiverID,
                                    snapshot.getChildrenCount(),
                                    false
                            )
                    );
                    if (!isGroup) {
                        FirebaseDatabase.getInstance().getReference().child("chatsDetails").child(receiverID)
                                .child(id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            FirebaseDatabase.getInstance().getReference().child("chatsDetails").child(receiverID).child(id).setValue(
                                                    new ChatsDetails(
                                                            id,
                                                            mUser.getUid(),
                                                            0,
                                                            false
                                                    )
                                            );
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }else{
                    Toast.makeText(MessageActivity.this, "No Messages Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void send(){
        if (uri == null){
            if (!TextUtils.isEmpty(textSend.getText().toString().trim())) {
                String key = databaseReference.push().getKey();
                String date = new SimpleDateFormat("hh:mm a").format(new Date());
                String time = new SimpleDateFormat("mm dd yyyy").format(new Date());
                databaseReference.child(key).setValue(
                        new MessageModel(
                                key,
                                mUser.getUid(),
                                mUser.getPhoneNumber(),
                                textSend.getText().toString().trim(),
                                time,
                                date,
                                null,
                                false,
                                false,
                                false,
                                false,
                                null,
                                null,
                                null
                        )
                );
                textSend.setText("");
            }else{
                Toast.makeText(MessageActivity.this, "Message Cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }else{

            final String key = databaseReference.push().getKey();
            final String date = new SimpleDateFormat("hh:mm a").format(new Date());
            final String time = new SimpleDateFormat("mm dd yyyy").format(new Date());
            thumbnail.setVisibility(View.GONE);
            storageReference.child(key).putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                storageReference.child(key).getDownloadUrl()
                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                databaseReference.child(key).setValue(
                                                        new MessageModel(
                                                                key,
                                                                mUser.getUid(),
                                                                mUser.getPhoneNumber(),
                                                                textSend.getText().toString().trim(),
                                                                time,
                                                                date,
                                                                task.getResult().toString(),
                                                                false,
                                                                false,
                                                                false,
                                                                false,
                                                                type,
                                                                uri.toString(),
                                                                Method.getFileName(MessageActivity.this, uri)
                                                        )
                                                );
                                                uri = null;
                                                textSend.setText("");
                                                Toast.makeText(MessageActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else{
                                Toast.makeText(MessageActivity.this, "File Not Sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void disable(View view) {
        card.setVisibility(View.GONE);
    }

    public void doc(View view) {
        card.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        startActivityForResult(Intent.createChooser(intent, "Select From"), PICK_IMAGE);
        type = "doc";
    }

    public void img(View view) {
        card.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select From"), PICK_IMAGE);
        type = "img";
    }

    public void video(View view) {
        card.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select From"), PICK_IMAGE);
        type = "video";
    }

    public void audio(View view) {
        card.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select From"), PICK_IMAGE);
        type = "audio";
    }

    public void contact(View view) {
        card.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(Intent.createChooser(intent, "Select From"), PICK_IMAGE);
        type = "contact";
    }

    public void location(View view) {
        card.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            uri = data.getData();
            thumbnail.setVisibility(View.VISIBLE);
            if (type.equals("img") || type.equals("video")) {
                    Glide.with(this).load(uri).thumbnail(.1f).into(thumbnail);
            }else{
                thumbnail.setImageDrawable(getResources().getDrawable(Method.getDrawable(Method.getFileName(this, uri))));
            }
        }
    }
}