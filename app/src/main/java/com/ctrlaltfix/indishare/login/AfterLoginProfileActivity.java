package com.ctrlaltfix.indishare.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.ChatHomeActivity;
import com.ctrlaltfix.indishare.ChatSection.Models.ChatsDetails;
import com.ctrlaltfix.indishare.ChatSection.Models.UsersModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.SendAnywhere;
import com.ctrlaltfix.indishare.UI.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AfterLoginProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;
    EditText profileName;
    EditText profileEmail;
    CircularImageView profileImage;

    static Uri uri = null;

    FirebaseUser mUser;

    LinearLayout loading;

    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileImage = findViewById(R.id.profileImage);
        loading = findViewById(R.id.loading);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAnalytics.getInstance(this);
        if (mUser.getDisplayName() != null){
            profileName.setText(mUser.getDisplayName());
        }

        if (mUser.getEmail() != null){
            profileEmail.setText(mUser.getEmail());
        }

        if (mUser.getPhotoUrl() != null){
            Glide.with(this).load(mUser.getPhotoUrl()).into(profileImage);
        }

    }

    public void proceedToHome(View view) {
        loading.setVisibility(View.VISIBLE);
        final String name = profileName.getText().toString().trim();
        final String email = profileEmail.getText().toString().trim().isEmpty() ? null : profileEmail.getText().toString().trim();
        if (name.isEmpty()){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
        }else{
            if (email != null){
                mUser.updateEmail(email);
            }
            if (uri != null) {
                final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImage/" + mUser.getUid());
                reference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        rootRef.child("ChatUsers").child(mUser.getUid()).setValue(
                                          new UsersModel(
                                                  mUser.getUid(),
                                                 mUser.getPhoneNumber(),
                                                 name,
                                                 false,
                                                 "Hey, I am using "+getResources().getString(R.string.app_name),
                                                  task.getResult().toString(),
                                                  email
                                          )
                                        );
                                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                        builder.setDisplayName(name);
                                        builder.setPhotoUri(task.getResult());
                                        mUser.updateProfile(builder.build())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loading.setVisibility(View.GONE);
                                                startActivity(
                                                        new Intent(AfterLoginProfileActivity.this, getIntent().getStringExtra("go").equals("chat") ? ChatHomeActivity.class :SendAnywhere.class)
                                                );
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                builder.setDisplayName(name);
                                mUser.updateProfile(builder.build())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loading.setVisibility(View.GONE);
                                        startActivity(
                                                new Intent(AfterLoginProfileActivity.this, getIntent().getStringExtra("go").equals("chat") ? ChatHomeActivity.class :SendAnywhere.class)
                                        );
                                    }
                                });
                            }
                        });
            }else{
                rootRef.child("ChatUsers").child(mUser.getUid()).setValue(
                        new UsersModel(
                                mUser.getUid(),
                                mUser.getPhoneNumber(),
                                name,
                                false,
                                "Hey, I am using "+getResources().getString(R.string.app_name),
                                null,
                                email
                        )
                );
                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                builder.setDisplayName(name);
                mUser.updateProfile(builder.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.setVisibility(View.GONE);
                        startActivity(
                                new Intent(AfterLoginProfileActivity.this, getIntent().getStringExtra("go").equals("chat") ? ChatHomeActivity.class :SendAnywhere.class)
                        );
                    }
                });
            }
        }
    }

    public void uploadImage(View view) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            uri = data.getData();
            Log.d("suyash", uri.toString());
            Glide.with(this).load(uri).into(profileImage);
        }
    }
}