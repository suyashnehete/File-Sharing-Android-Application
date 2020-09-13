package com.ctrlaltfix.indishare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Adapters.AppViewAdapter;
import com.ctrlaltfix.indishare.Adapters.AudioViewAdapter;
import com.ctrlaltfix.indishare.Adapters.ImageViewAdapter;
import com.ctrlaltfix.indishare.Adapters.TrackSendAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.Fragments.AppItemFragment;
import com.ctrlaltfix.indishare.Fragments.AudioItemFragment;
import com.ctrlaltfix.indishare.Fragments.FileItemFragment;
import com.ctrlaltfix.indishare.Fragments.ImageItemFragment;
import com.ctrlaltfix.indishare.Fragments.LinksItemFragment;
import com.ctrlaltfix.indishare.Fragments.ReceiveFragment;
import com.ctrlaltfix.indishare.Fragments.TrackSendBottomSheetFragment;
import com.ctrlaltfix.indishare.Fragments.VideoItemFragment;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.ShareCodeModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.Models.TrackUserFileModel;
import com.ctrlaltfix.indishare.Models.UserFileModel;
import com.ctrlaltfix.indishare.UI.CircularImageView;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class SendAnywhere extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    BottomNavigationView navigationView;
    TabLayout tabs;
    Button send;
    FloatingActionButton fab;

    CircularImageView toolbarImage;

    static int position = 0;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    String userID;
    UploadTask uploadTask;
    public static boolean isRunning = false;
    public static boolean isSending = false;
    public static int constantSize = 0;
    public static int constIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_anywhere);

        navigationView = findViewById(R.id.bottomNavigation);
        tabs = findViewById(R.id.tabs);
        send = findViewById(R.id.send);
        toolbarImage = findViewById(R.id.toolbarImage);
        fab = findViewById(R.id.fab);

        loadFragment(setTabLayout(position));

        FirebaseAnalytics.getInstance(this);

        navigationView.setOnNavigationItemSelectedListener(this);
        tabs.addOnTabSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mStorage = FirebaseStorage.getInstance().getReference().child(userID);

        if (mUser.getPhotoUrl() != null){
            Glide.with(this).load(mUser.getPhotoUrl()).into(toolbarImage);
        }
    }

    private Fragment setTabLayout(int position) {
        if (position == 0){
            return new AppItemFragment();
        }else if (position == 1){
            return new VideoItemFragment();
        }else if (position == 2){
            return new ImageItemFragment();
        }else if (position == 3){
            return new AudioItemFragment();
        }else if (position == 4){
            return new FileItemFragment();
        }
        return new AppItemFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.page_1 :
                if (isSending)
                    fab.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
                loadFragment(setTabLayout(position));
                break;
            case R.id.page_2 :
                fab.setVisibility(View.GONE);
                tabs.setVisibility(View.GONE);
                send.setVisibility(View.GONE);
                loadFragment(new ReceiveFragment().newInstance());
                break;
                case R.id.page_3 :
                    fab.setVisibility(View.GONE);
                tabs.setVisibility(View.GONE);
                send.setVisibility(View.GONE);
                loadFragment(new LinksItemFragment().newInstance());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
        }
        Log.d("suyash", position+"");
        return true;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.view_pager, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        position = tab.getPosition();
        loadFragment(setTabLayout(tab.getPosition()));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void onSend(View v){
        if (Constant.sendUri.size() != 0){
            if (AppItemFragment.width != 0 && AppItemFragment.recyclerView != null) {
                AppItemFragment.adapter = new AppViewAdapter(Constant.appList, getApplicationContext(), AppItemFragment.width);
                AppItemFragment.recyclerView.setAdapter(AppItemFragment.adapter);
            }
            if (AudioItemFragment.adapter != null && AudioItemFragment.recyclerView != null) {
                AudioItemFragment.adapter = new AudioViewAdapter(Constant.allAudioListModificationTime, getApplicationContext());
                AudioItemFragment.recyclerView.setAdapter(AudioItemFragment.adapter);
            }
            if (ImageItemFragment.width != 0 && ImageItemFragment.recyclerView != null) {
                ImageItemFragment.adapter = new ImageViewAdapter(Constant.allImageListModificationTime, getApplicationContext(), ImageItemFragment.width);
                ImageItemFragment.recyclerView.setAdapter(ImageItemFragment.adapter);
            }
            if (VideoItemFragment.adapter != null && VideoItemFragment.recyclerView != null) {
                VideoItemFragment.adapter = new VideoViewAdapter(Constant.allVideoListModificationTime, getApplicationContext());
                VideoItemFragment.recyclerView.setAdapter(VideoItemFragment.adapter);
            }
            if (FileItemFragment.recyclerView != null) {
                Method.getDirFromRoot(FileItemFragment.fileList.get(0).toString(), SendAnywhere.this, FileItemFragment.fileList.get(0).toString());
            }
            if (!isRunning) {
                for (int i = 0; i < Constant.sendUriDetails.size(); i++) {

                    String uri;

                    if (Constant.sendOriginalUri.size() > i) {
                        uri = Constant.sendOriginalUri.get(i).toString();
                        Constant.trackUserFileModels.add(
                                new TrackUserFileModel(
                                        Constant.sendUriDetails.get(i).getName(),
                                        Constant.sendUriDetails.get(i).getSizeInBytes(),
                                        Constant.sendUriDetails.get(i).getType(),
                                        "",
                                        uri,
                                        null
                                )
                        );
                    }

                }
            }else{
                for (int i = constantSize; i < Constant.sendUriDetails.size(); i++) {
                    String uri;
                    if (Constant.sendOriginalUri.size() > i) {
                        uri = Constant.sendOriginalUri.get(i).toString();
                        Constant.trackUserFileModels.add(
                                new TrackUserFileModel(
                                        Constant.sendUriDetails.get(i).getName(),
                                        Constant.sendUriDetails.get(i).getSizeInBytes(),
                                        Constant.sendUriDetails.get(i).getType(),
                                        "",
                                        uri,
                                        null
                                )
                        );
                    }

                }
            }
            TrackSendBottomSheetFragment.adapter = new TrackSendAdapter(Constant.trackUserFileModels, this);
            if (TrackSendBottomSheetFragment.recyclerView != null){
                TrackSendBottomSheetFragment.recyclerView.setAdapter(TrackSendBottomSheetFragment.adapter);
            }
            constantSize = Constant.sendUriDetails.size();
            if (!isRunning) {
                constIndex++;
                fab.setVisibility(View.VISIBLE);
                isRunning = true;
                isSending = true;
                sendFile(0);
            }
        }else{
            Toast.makeText(this, "No Items Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendFile(final int index){
        final String shareCode = randomShareCodeGenerator();
        FirebaseDatabase.getInstance().getReference().child("shareCodes").orderByKey().equalTo(shareCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            sendFile(index);
                        }else{
                            uploadTask = mStorage.child(Constant.sendUriDetails.get(index).getName()+new Date().toString()+Calendar.getInstance().getTimeInMillis()).putFile(Constant.sendUri.get(index));
                            if(Constant.trackUserFileModels.size() > constIndex) {
                                Constant.trackUserFileModels.get(constIndex).setUploadTask(uploadTask);
                                Constant.trackUserFileModels.get(constIndex).setShareCode(shareCode);
                                Log.d("suyash", constIndex+"");
                                Log.d("suyash", index+"");
                            }else{
                                uploadTask.cancel();
                                if ((Constant.sendUri.size() - 1) > index) {
                                    sendFile(index);
                                } else {
                                    constantSize++;
                                    isRunning = false;
                                    Constant.sendOriginalUri.clear();
                                    Constant.sendUri.clear();
                                    Constant.sendUriDetails.clear();
                                }
                            }
                            Log.d("suyash", shareCode);
                            TrackSendBottomSheetFragment.adapter = new TrackSendAdapter(Constant.trackUserFileModels, SendAnywhere.this);
                            if (TrackSendBottomSheetFragment.recyclerView != null){
                                TrackSendBottomSheetFragment.recyclerView.setAdapter(TrackSendBottomSheetFragment.adapter);
                            }
                            if (!uploadTask.isCanceled()) {
                                uploadTask
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                uploadTask.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        String key = mDatabase.push().getKey();
                                                        FirebaseDatabase.getInstance().getReference().child("shareCodes").child(shareCode)
                                                                .setValue(new ShareCodeModel(userID, key));

                                                        mDatabase.child(key).setValue(
                                                                new UserFileModel(
                                                                        Constant.sendUriDetails.get(index).getName(),
                                                                        Constant.sendUriDetails.get(index).getSizeInBytes(),
                                                                        task.getResult().toString(),
                                                                        Calendar.getInstance().getTimeInMillis(),
                                                                        Constant.sendUriDetails.get(index).getType(),
                                                                        shareCode,
                                                                        Constant.sendOriginalUri.get(index).toString()
                                                                )
                                                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if ((Constant.sendUri.size() - 1) > index) {
                                                                    constIndex++;
                                                                    sendFile(index + 1);
                                                                } else {
                                                                    constantSize++;
                                                                    isRunning = false;
                                                                    Constant.sendOriginalUri.clear();
                                                                    Constant.sendUri.clear();
                                                                    Constant.sendUriDetails.clear();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        })
                                        .addOnCanceledListener(new OnCanceledListener() {
                                            @Override
                                            public void onCanceled() {
                                                if ((Constant.sendUri.size() - 1) > index) {
                                                    sendFile(index);
                                                } else {
                                                    constantSize++;
                                                    isRunning = false;
                                                    Constant.sendOriginalUri.clear();
                                                    Constant.sendUri.clear();
                                                    Constant.sendUriDetails.clear();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if ((Constant.sendUri.size() - 1) > index) {
                                                    sendFile(index + 1);
                                                    constIndex++;
                                                } else {
                                                    constantSize++;
                                                    isRunning = false;
                                                    Constant.sendOriginalUri.clear();
                                                    Constant.sendUri.clear();
                                                    Constant.sendUriDetails.clear();
                                                }
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

    public void cancelTransaction(){
        if (uploadTask!=null){
            uploadTask.cancel();
        }
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        finishAffinity();
        startActivity(
                new Intent(this, MainActivity.class)
        );
    }

    public String randomShareCodeGenerator(){
        Random random = new Random();
        long sharecode = random.nextInt(999999);
        return String.format("%06d", sharecode);
    }

    public void onFabClicked(View view) {
        TrackSendBottomSheetFragment trackSendBottomSheetFragment = new TrackSendBottomSheetFragment();
        trackSendBottomSheetFragment.show(getSupportFragmentManager(), trackSendBottomSheetFragment.getTag());
    }
}