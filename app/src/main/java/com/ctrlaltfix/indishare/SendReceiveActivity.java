package com.ctrlaltfix.indishare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.Adapters.AppViewAdapter;
import com.ctrlaltfix.indishare.Adapters.AudioViewAdapter;
import com.ctrlaltfix.indishare.Adapters.ImageViewAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.BroadcastReceivers.SendRecevieBroadcastReceiver;
import com.ctrlaltfix.indishare.Adapters.SectionsPagerAdapter;
import com.ctrlaltfix.indishare.Fragments.AppItemFragment;
import com.ctrlaltfix.indishare.Fragments.AudioItemFragment;
import com.ctrlaltfix.indishare.Fragments.FileItemFragment;
import com.ctrlaltfix.indishare.Fragments.ImageItemFragment;
import com.ctrlaltfix.indishare.Fragments.TrackBottomSheetFragment;
import com.ctrlaltfix.indishare.Fragments.VideoItemFragment;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.DetailsTransferService;
import com.ctrlaltfix.indishare.Utils.LoadData;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import static com.ctrlaltfix.indishare.Utils.Method.getFile;

public class SendReceiveActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {


    WifiP2pInfo info;


    Button send;
    ImageButton disconnect;
    TextView nameTextview;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    WifiManager wifiManager;

    public static ServerSocket serverSocket;
    public static ServerSocket server;

    public static boolean isRunning = false;

    public static FloatingActionButton fab;
    String name = "";

    public static int  dataIndex = 0;
    public static ArrayList<SendFileDetailsModel> sendFileDetailsModels = new ArrayList<>();

    public static Handler rootHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_receive);
        Intent i = getIntent();
        info = (WifiP2pInfo) i.getParcelableExtra("info");
        name = i.getStringExtra("name");
        Constant.adminAddress = info.groupOwnerAddress;
        Constant.isGroupOwner = info.isGroupOwner;
        Constant.trackDataIndex = 0;
        Constant.constantSize = 0;
        Constant.isRunning = false;
        try {

            server = info.isGroupOwner ? new ServerSocket(8999) : new ServerSocket(8998);
            serverSocket = info.isGroupOwner ? new ServerSocket(8888) : new ServerSocket(8988);

            Thread getFile = new Thread(new GetFile(this, serverSocket, server));
            getFile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Suyash", "level1");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        Log.d("Suyash", "level2");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Log.d("Suyash", "level3");
        manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(SendReceiveActivity.this, getMainLooper(), null);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Log.d("Suyash", "level4");
        send = findViewById(R.id.send);
        disconnect = findViewById(R.id.disconnect);
        fab = findViewById(R.id.fab);
        nameTextview = findViewById(R.id.name);
        nameTextview.setText(name);
        Log.d("Suyash", "level5");
        onAction();
        Log.d("suyash", info.isGroupOwner+"fghj");
        if (!Constant.isGroupOwner) {
            Log.d("suyash", "coming");
            getAddress();
        }
        Log.d("suyash", Constant.sendUsingSend.size()+"");
        if (Constant.sendUsingSend.size() != 0){
            Log.d("suyash","sending");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendExtra();
        }
    }

    private void sendExtra(){
        Constant.sendUri.addAll(Constant.sendUsingSend);
        Constant.sendOriginalUri.addAll(Constant.sendUsingOriginalSend);
        Constant.sendUriDetails.addAll(Constant.sendAllUsingFiles);

        Constant.onlyForServer.clear();
        if (!Constant.isRunning) {
            for (int i = 0; i < Constant.sendUriDetails.size(); i++) {
                SendFileDetailsModel a = Constant.sendUriDetails.get(i);
                Constant.onlyForServer.add(a);
                Uri uri;
                if (Constant.sendOriginalUri.size() > i) {
                    uri = Constant.sendOriginalUri.get(i);
                    Constant.trackDataModels.add(new TrackDataModel(a.getName(), 0, a.getSize(), uri, 0, a.getType(), a.getSizeInBytes(), "Sent"));
                }

            }
        }else{
            for (int i = Constant.constantSize; i < Constant.sendUriDetails.size(); i++) {
                SendFileDetailsModel a = Constant.sendUriDetails.get(i);
                Constant.onlyForServer.add(a);
                Uri uri;
                if (Constant.sendOriginalUri.size() > i) {
                    uri = Constant.sendOriginalUri.get(i);
                    Constant.trackDataModels.add(new TrackDataModel(a.getName(), 0, a.getSize(), uri, 0, a.getType(), a.getSizeInBytes(), "Sent"));
                }

            }
        }
        fab.show();
        if (Constant.sendUri.size() > 0) {
            Constant.constantSize = Constant.sendUriDetails.size();
            Thread thread = new Thread(new DetailsTransferService(this));
            thread.start();
        } else {
            Toast.makeText(SendReceiveActivity.this, "no files selected", Toast.LENGTH_SHORT).show();
        }

        Constant.sendUsingSend.clear();
        Constant.sendAllUsingFiles.clear();
        Constant.sendUsingOriginalSend.clear();
    }

    private void getAddress() {
        File f = new File(getExternalFilesDir("received"),
                "indishare");
        File dirs = new File(f.getParent());
        if (!dirs.exists())
            dirs.mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Constant.sendUri.add(Uri.fromFile(f));
        Constant.sendOriginalUri.add(Uri.fromFile(f));
        Constant.sendUriDetails.add(new SendFileDetailsModel(f.getName(), f.length() + "", "temp", 0));
        Log.d("suyash", "sending");
        Thread thread = new Thread(new DetailsTransferService(this));
        thread.start();
    }


    private void onAction() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Method.getDirFromRoot(FileItemFragment.fileList.get(0).toString(), SendReceiveActivity.this, FileItemFragment.fileList.get(0).toString());
                }
                Constant.onlyForServer.clear();
                if (!Constant.isRunning) {
                    for (int i = 0; i < Constant.sendUriDetails.size(); i++) {
                        SendFileDetailsModel a = Constant.sendUriDetails.get(i);
                        Constant.onlyForServer.add(a);
                        Uri uri;
                        if (Constant.sendOriginalUri.size() > i) {
                            uri = Constant.sendOriginalUri.get(i);
                            Constant.trackDataModels.add(new TrackDataModel(a.getName(), 0, a.getSize(), uri, 0, a.getType(), a.getSizeInBytes(), "Sent"));
                        }

                    }
                }else{
                    for (int i = Constant.constantSize; i < Constant.sendUriDetails.size(); i++) {
                        SendFileDetailsModel a = Constant.sendUriDetails.get(i);
                        Constant.onlyForServer.add(a);
                        Uri uri;
                        if (Constant.sendOriginalUri.size() > i) {
                            uri = Constant.sendOriginalUri.get(i);
                            Constant.trackDataModels.add(new TrackDataModel(a.getName(), 0, a.getSize(), uri, 0, a.getType(), a.getSizeInBytes(), "Sent"));
                        }

                    }
                }
                fab.show();
                if (Constant.sendUri.size() > 0) {
                    Constant.constantSize = Constant.sendUriDetails.size();
                    Thread thread = new Thread(new DetailsTransferService(SendReceiveActivity.this));
                    thread.start();
                } else {
                    Toast.makeText(SendReceiveActivity.this, "no files selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackBottomSheetFragment trackBottomSheetFragment = new TrackBottomSheetFragment();
                trackBottomSheetFragment.show(getSupportFragmentManager(), trackBottomSheetFragment.getTag());
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disconnect();
            }
        });
    }

    public void disconnect() {

        Constant.sendUri.clear();
        Constant.sendOriginalUri.clear();
        Constant.sendUriDetails.clear();
        Constant.address = null;
        Constant.adminAddress = null;
        Constant.isGroupOwner = null;
        Constant.trackDataIndex = 0;
        Constant.trackDataModels.clear();
        Constant.isRunning = false;
        Constant.onlyForServer.clear();
        Constant.constantSize = 0;
        Constant.sendUsingSend.clear();
        Constant.sendAllUsingFiles.clear();
        Constant.sendUsingOriginalSend.clear();
        dataIndex = 0;
        isRunning = false;

        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
            }

            @Override
            public void onSuccess() {
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        receiver = new SendRecevieBroadcastReceiver(wifiManager, manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.info = info;
    }

    @Override
    public void onBackPressed() {
        disconnect();
        finishAffinity();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        finishAffinity();
    }

    class GetFile implements Runnable{
        private Context context;
        ServerSocket serverSocket;
        ServerSocket server;

        public GetFile(Context context, ServerSocket serverSocket, ServerSocket server) {
            this.context = context;
            this.serverSocket = serverSocket;
            this.server = server;
            Log.d("suyash", "entered");
        }
        @Override
        public void run() {
            while (serverSocket != null) {
                Log.d("suyash", "hii");
                try {
                    Socket filename = server.accept();

                    Constant.address = filename.getInetAddress().getHostAddress();
                    ObjectInputStream objectInputStream = new ObjectInputStream(filename.getInputStream());
                    ArrayList<SendFileDetailsModel> model = (ArrayList<SendFileDetailsModel>) objectInputStream.readObject();

                    sendFileDetailsModels.addAll(model);

                    if (model.size()!=0 && !model.get(0).getType().equals("temp")) {
                        for (int i = 0; i < model.size(); i++) {
                            SendFileDetailsModel a = model.get(i);
                            Constant.trackDataModels.add(new TrackDataModel(a.getName(), 0, a.getSize(), null, 0, a.getType(), a.getSizeInBytes(), "Received"));
                        }
                        rootHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                fab.show();
                            }
                        });
                    }
                    filename.close();

                    if (!isRunning){
                        isRunning = true;
                        Thread thread = new Thread(new DownloadFile(serverSocket, context));
                        thread.start();
                    }

                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DownloadFile implements Runnable{
        ServerSocket serverSocket;
        Context context;
        int len;
        int index;
        boolean done;

        public DownloadFile(ServerSocket serverSocket, Context context) {
            this.serverSocket = serverSocket;
            this.context = context;
        }

        @Override
        public void run() {
            try{
                while(sendFileDetailsModels.size() > dataIndex) {
                    Socket client = serverSocket.accept();
                    done = false;
                    SendFileDetailsModel m = sendFileDetailsModels.get(dataIndex);
                    final File f = getFile(context, m.getType(), m.getName());
                    index = 0;
                    Log.d("suyash", "-------------------------------------------------");
                    for (int j = 0; j < Constant.trackDataModels.size(); j++) {
                        if (Constant.trackDataModels.get(j).getName().equals(m.getName()) && Constant.trackDataModels.get(j).getProgress() == 0) {
                            index = j;
                            Log.d("suyash", index+"");
                            Log.d("suyash", j+"");
                            Constant.trackDataModels.get(j).setUri(Uri.fromFile(f));
                            break;
                        }else{
                            Log.d("suyash1", Constant.trackDataModels.get(j).getName());
                            Log.d("suyash2", m.getName());
                        }
                    }
                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    Log.d("TAG", "server: copying files " + f.toString());
                    InputStream inputstream = client.getInputStream();
                    FileOutputStream out = new FileOutputStream(f);
                    byte buf[] = new byte[1024];
                    while ((len = inputstream.read(buf)) != -1) {
                        done = true;
                        out.write(buf, 0, len);
                        Method.run(len, index);
                    }
                    out.close();
                    inputstream.close();
                    client.close();
                    if (done) {
                        dataIndex++;
                    }
                }
                isRunning = false;
                if (sendFileDetailsModels.size() == dataIndex) {
                    sendFileDetailsModels.clear();
                    dataIndex = 0;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}