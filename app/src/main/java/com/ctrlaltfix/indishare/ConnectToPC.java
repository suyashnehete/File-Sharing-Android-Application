package com.ctrlaltfix.indishare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.Adapters.AppViewAdapter;
import com.ctrlaltfix.indishare.Adapters.AudioViewAdapter;
import com.ctrlaltfix.indishare.Adapters.ImageViewAdapter;
import com.ctrlaltfix.indishare.Adapters.SectionsPagerAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.BroadcastReceivers.ConnectToPCBroadcastReceiver;
import com.ctrlaltfix.indishare.Fragments.AppItemFragment;
import com.ctrlaltfix.indishare.Fragments.AudioItemFragment;
import com.ctrlaltfix.indishare.Fragments.FileItemFragment;
import com.ctrlaltfix.indishare.Fragments.ImageItemFragment;
import com.ctrlaltfix.indishare.Fragments.TrackBottomSheetFragment;
import com.ctrlaltfix.indishare.Fragments.VideoItemFragment;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.FileTransferService;
import com.ctrlaltfix.indishare.Utils.LoadData;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;

import static com.ctrlaltfix.indishare.Utils.Method.getFile;
import static com.ctrlaltfix.indishare.Utils.Method.run;

public class ConnectToPC extends AppCompatActivity implements WifiP2pManager.GroupInfoListener {

    final static int WIFI_REQUEST_CODE = 200;
    public final static int PORT = 8080;
    public WifiManager wifiManager;

    int chances = 2;

    private static final String TAG = "Suyash";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    private static ArrayList<String> htmlCode = new ArrayList<>();
    private HttpServer mHttpServer;

    ImageButton disconnect;
    Button send;
    TextView name;
    TextView hotspot;
    TextView password;
    FloatingActionButton fab;
    static String filename="abc";
    static String filetype="abc";
    static String fileSize="0B";
    static int fileSizeInBytes=0;

    static boolean shown = false;

    Handler handler = new Handler();


    public void disconnect(){
        shown = false;
        Constant.sendUri.clear();
        Constant.sendUriDetails.clear();
        Constant.sendOriginalUri.clear();
        Constant.trackDataModels.clear();
        Constant.trackDataIndex = 0;
        manager.removeGroup(channel, null);
        channel = null;
        manager = null;
        stopServer();
        finishAffinity();
        startActivity(
                new Intent(ConnectToPC.this, MainActivity.class)
        );
    }
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    finishAffinity();
                    onBackPressed();
                }
                break;
        }
    }

    private boolean initP2p() {
        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }

        // Hardware capability check
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!wifiManager.isP2pSupported()) {
                Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
                return false;
            }
        }


        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }

        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connec_to_p_c);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        new LoadData().execute();

        initialization();
        startBroadcast();
        if (!wifiManager.isWifiEnabled()) {
            turnONWiFi();
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Method.handleSendText(intent, this); // Handle text being sent
            } else {
                Method.handleSendImage(intent, this); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Method.handleSendMultipleText(intent, this); // Handle multiple images being sent
            }else{
                Method.handleSendMultipleImages(intent, this); // Handle multiple images being sent
            }
        }

        if (Constant.sendUsingSend.size() != 0){
            for (int i = 0 ; i < Constant.sendUsingSend.size(); i++){
                String str = "<div id=\"data\">\n" +
                        "                    <img class='"+Constant.sendAllUsingFiles.get(i).getType()+"'>\n" +
                        "                    <p>"+Constant.sendAllUsingFiles.get(i).getName()+"</p>\n" +
                        "                    <button onclick=\"download('"+Constant.sendUsingSend.get(i).getPath()+"', '"+Constant.sendAllUsingFiles.get(i).getName()+"')\">Download</button>\n" +
                        "                </div>";
                htmlCode.add(str);
                SendFileDetailsModel model = Constant.sendAllUsingFiles.get(i);
                TrackDataModel trackDataModel = new TrackDataModel(model.getName(), 0, model.getSize(), Constant.sendUsingOriginalSend.get(i), 0, model.getType(), model.getSizeInBytes(), "Sent");
                if (!Constant.trackDataModels.contains(trackDataModel)) {
                    Constant.trackDataModels.add(trackDataModel);
                }
            }
            Constant.sendUsingSend.clear();
            Constant.sendAllUsingFiles.clear();
            Constant.sendUsingOriginalSend.clear();
            Toast.makeText(ConnectToPC.this, "Refresh Your Browser WebPage.", Toast.LENGTH_SHORT).show();
            fab.show();
        }
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
                if (Constant.sendUri.size() != 0){
                    for (int i = 0 ; i < Constant.sendUri.size(); i++){
                        String str = "<div id=\"data\">\n" +
                                "                    <img class='"+Constant.sendUriDetails.get(i).getType()+"'>\n" +
                                "                    <p>"+Constant.sendUriDetails.get(i).getName()+"</p>\n" +
                                "                    <button onclick=\"download('"+Constant.sendUri.get(i).getPath()+"', '"+Constant.sendUriDetails.get(i).getName()+"')\">Download</button>\n" +
                                "                </div>";
                        htmlCode.add(str);
                        SendFileDetailsModel model = Constant.sendUriDetails.get(i);
                        TrackDataModel trackDataModel = new TrackDataModel(model.getName(), 0, model.getSize(), Constant.sendOriginalUri.get(i), 0, model.getType(), model.getSizeInBytes(), "Sent");
                        if (!Constant.trackDataModels.contains(trackDataModel)) {
                            Constant.trackDataModels.add(trackDataModel);
                        }
                    }
                    Constant.sendUri.clear();
                    Constant.sendUriDetails.clear();
                    Constant.sendOriginalUri.clear();
                    Toast.makeText(ConnectToPC.this, "Refresh Your Browser WebPage.", Toast.LENGTH_SHORT).show();
                    fab.show();
                }else{
                    Toast.makeText(ConnectToPC.this, "No Items Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackBottomSheetFragment fragment = new TrackBottomSheetFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void assign(Context context) {

        Constant.appList = Method.getInstalledApps(context);
        Constant.allAudioListModificationTime = Method.getAudio(context, MediaStore.Audio.Media.DATE_MODIFIED + " DESC");
        Constant.allVideoListModificationTime = Method.getVideo(context, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        Constant.allImageListModificationTime = Method.getImages(context, MediaStore.Images.Media.DATE_MODIFIED + " DESC");


        /*Constant.allAudioListA_Z = Method.getAudio(context, MediaStore.Audio.Media.DISPLAY_NAME + " ASC");
        Constant.allVideoListA_Z = Method.getVideo(context, MediaStore.Video.Media.DISPLAY_NAME + " ASC");
        Constant.allImageListA_Z = Method.getImages(context, MediaStore.Images.Media.DISPLAY_NAME + " ASC");

        Constant.allAudioListZ_A = Method.getAudio(context, MediaStore.Audio.Media.DISPLAY_NAME + " DESC");
        Constant.allVideoListZ_A = Method.getVideo(context, MediaStore.Video.Media.DISPLAY_NAME + " DESC");
        Constant.allImageListZ_A = Method.getImages(context, MediaStore.Images.Media.DISPLAY_NAME + " DESC");

        Constant.allAudioListFirst = Method.getAudio(context, MediaStore.Audio.Media.SIZE + " ASC");
        Constant.allVideoListFirst = Method.getVideo(context, MediaStore.Video.Media.SIZE + " ASC");
        Constant.allImageListFirst = Method.getImages(context, MediaStore.Images.Media.SIZE + " ASC");

        Constant.allAudioListLarge = Method.getAudio(context, MediaStore.Audio.Media.SIZE + " DESC");
        Constant.allVideoListLarge = Method.getVideo(context, MediaStore.Video.Media.SIZE + " DESC");
        Constant.allImageListLarge = Method.getImages(context, MediaStore.Images.Media.SIZE + " DESC");*/
    }


    public void startBroadcast() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        if (!initP2p()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(R.string.not_supported);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                alertDialog.show();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    this.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    private void turnONWiFi() {
        // check version code for enabling wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectToPC.this);
            alertDialog.setTitle("Enable Wifi");
            alertDialog.setMessage("Your android version does not allow to turn on wifi automatically");
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                }
            });
            alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivityForResult(intent, WIFI_REQUEST_CODE);
                }
            });
            alertDialog.show();

        } else {
            if (!wifiManager.isWifiEnabled()) {
                if (wifiManager.setWifiEnabled(true)) {
                    chances = 2;
                } else {
                    chances--;
                    if (chances != 0) {
                        turnONWiFi();
                    } else {
                        onBackPressed();
                    }
                }
            }
        }
    }

    private void initialization() {

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        name = findViewById(R.id.name);
        disconnect = findViewById(R.id.disconnect);
        send = findViewById(R.id.send);
        hotspot = findViewById(R.id.hotspot);
        password = findViewById(R.id.password);
        fab = findViewById(R.id.fab);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WIFI_REQUEST_CODE) {
            if (!wifiManager.isWifiEnabled()) {
                turnONWiFi();
            }
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new ConnectToPCBroadcastReceiver(wifiManager, manager, channel, ConnectToPC.this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        if (manager != null){
            disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null){
            disconnect();
        }
    }

    public void connect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiP2pConfig.Builder builder = new WifiP2pConfig.Builder();
            builder.setNetworkName("DIRECT-NS-IndiShare");
            builder.setPassphrase("IndiShare");
            manager.createGroup(channel, builder.build(), null);
        } else {
            manager.createGroup(channel, null);
        }
        manager.requestGroupInfo(channel, this);
    }

    public void discover() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(ConnectToPC.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(ConnectToPC.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////
    private String streamToString(InputStream inputStream){
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String stream = "";
        if (s.hasNext()){
            stream = s.next();
        }
        return stream;
    }

    private void sendResponse(HttpExchange httpExchange, String reponseText){
        try {
            httpExchange.sendResponseHeaders(200, reponseText.length());
            OutputStream os = httpExchange.getResponseBody();
            if (reponseText != null){
                byte[] data = reponseText.getBytes();
                os.write(data);
                os.close();
            }
        } catch (IOException e) {
            Log.d("suyash", e.toString());
        }
    }

    public void startServer(int port){
        try{
            mHttpServer = HttpServer.create(new InetSocketAddress(port), 5000);
            if (mHttpServer == null){
                return;
            }
            mHttpServer.setExecutor(Executors.newCachedThreadPool());
            mHttpServer.createContext("/", this.rootHandler);
            mHttpServer.createContext("/upload", this.uploadHandler);
            mHttpServer.createContext("/download", this.downloadHandler);
            mHttpServer.start();
            name.setText("http://192.168.49.1:8080");
        } catch (IOException e) {
            Log.d("suyash", e.toString());
        }
    }

    private void stopServer(){
        if (mHttpServer != null){
            mHttpServer.stop(0);
        }
    }

    private void show(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                fab.show();
            }
        });
    }

    private HttpHandler uploadHandler = new HttpHandler() {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            show();
            switch (httpExchange.getRequestMethod()){
                case "GET":
                    sendResponse(httpExchange, "");
                    filename = httpExchange.getRequestURI().getQuery().split("&")[0].split("=")[1];
                    filetype = httpExchange.getRequestURI().getQuery().split("&")[1].split("=")[1];
                    fileSizeInBytes = Integer.parseInt(httpExchange.getRequestURI().getQuery().split("&")[2].split("=")[1]);
                    fileSize = httpExchange.getRequestURI().getQuery().split("&")[3].split("=")[1];
                    sendResponse(httpExchange, "");
                    Log.d("suyash", filename);
                    Log.d("suyash", filetype);
                    Log.d("suyash", fileSize);
                    Log.d("suyash", fileSizeInBytes+"");
                    break;
                case "POST":
                    Log.d("suyash","done");
                    InputStream ios = httpExchange.getRequestBody();

                    byte[] bytes = new byte[1024];
                    int i;
                    final File f = getFile(ConnectToPC.this, filetype, filename);
                    TrackDataModel trackDataModel = new TrackDataModel(filename, 0, fileSize, Uri.fromFile(f), 0, filetype, fileSizeInBytes, "Receive");
                    Constant.trackDataModels.add(trackDataModel);
                    int index = Constant.trackDataModels.indexOf(trackDataModel);
                    Log.d("suyash","level2");
                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    while ((i = ios.read(bytes)) != -1) {
                        out.write(bytes, 0, i);
                        run(i, index);
                    }
                    sendResponse(httpExchange, "");
                    break;
            }
        }
    };

    private HttpHandler rootHandler = new HttpHandler() {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String file = "</div>\n" +
                    "        </div>\n" +
                    "        <div id='sent-section'>\n" +
                    "            <div id='heading'>\n" +
                    "                <h3>Files Sent</h3>\n" +
                    "            </div>\n" +
                    "            <div id='sentcontent'>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </section>\n" +
                    "    <script>\n" +
                    " var content = document.getElementById('sentcontent');" +
                    "       var cookies = document.cookie.split(';');\n" +
                    "            for (var i = 0; i < cookies.length; i++) {\n" +
                    "                           if(cookies[i] !== ''){" +
                    "                           var data = document.createElement('div');\n" +
                    "                           data.id = 'data';" +
                    "                           var img = document.createElement('img');" +
                    "                           img.className = cookies[i].split(\"=\")[1].split(\"...\")[0];" +
                    "                           data.append(img);\n" +
                    "                            var textview = document.createElement('p');\n" +
                    "                            textview.innerHTML = cookies[i].split(\"=\")[0].trim();\n" +
                    "                            var percentage = document.createElement('p');\n" +
                    "                            percentage.innerHTML = cookies[i].split(\"=\")[1].split(\"...\")[1];\n" +
                    "                            percentage.style.width = '20%';\n" +
                    "                            percentage.style.fontSize = '20px';\n" +
                    "                            percentage.style.textAlign = 'right';\n" +
                    "                            data.append(textview);\n" +
                    "                            data.append(percentage);\n" +
                    "                            content.append(data);" +
                    "}" +
                    "            }\n" +
                    "        var abcd = document.getElementsByClassName('Videos');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = video;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('Audio');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = audio;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('Images');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = image;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('Other');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = other;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('Documents');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = documents;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('Compressed');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = compressed;\n" +
                    "        }\n" +
                    "        abcd = document.getElementsByClassName('APK');\n" +
                    "        for (i = 0; i < abcd.length; i++) {\n" +
                    "            abcd[i].src = apk;\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            AssetManager am = getAssets();
            InputStream in = am.open("index.txt");
            File f = new File(getExternalFilesDir(null), "abc.html");
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
            for (String str : htmlCode) {
                BufferedWriter out1 = new BufferedWriter(new FileWriter(f, true));
                out1.write(str);
                out1.close();
            }
            BufferedWriter out1 = new BufferedWriter(new FileWriter(f, true));
            out1.write(file);
            out1.close();

            String response = streamToString(new FileInputStream(f));
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            f.delete();
        }
    };

    private HttpHandler downloadHandler = new HttpHandler() {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getQuery().split("=")[1];
            Log.d("suyash", path);
            File file = new File (path);
            byte [] bytearray  = new byte [1024];
            int len;
            int index = 0;
            for (int i = 0; i<Constant.trackDataModels.size(); i++){
                Log.d("suyash", Constant.trackDataModels.get(i).getName());
                if (Constant.trackDataModels.get(i).getName().equals(file.getName()) && Constant.trackDataModels.get(i).getProgress() == 0) {
                    index = i;
                    break;
                }
            }
            if (index == 0){
                for (int i = 0; i<Constant.trackDataModels.size(); i++){
                    Log.d("suyash", Constant.trackDataModels.get(i).getName());
                    if (Constant.trackDataModels.get(i).getType().equals("APK") && Constant.trackDataModels.get(i).getProgress() == 0) {
                        index = i;
                        break;
                    }
                }
            }
            Log.d("suyash", index+"");
            FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(Uri.fromFile(file));
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            while ((len = fis.read(bytearray)) != -1) {
                os.write(bytearray, 0, len);
                run(len, index);
            }
            os.close();
        }
    };

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        if (group != null && group.isGroupOwner()) {
            hotspot.setText("Hotspot: "+group.getNetworkName());
            password.setText("Password: "+group.getPassphrase());
            if (!shown) {
                shown = true;
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Connect To PC");
                alert.setMessage("Connect your PC to the following Hotspot.\nHotspot: " + group.getNetworkName() + "\nPassword: " + group.getPassphrase() + "\nAnd visit to http://192.168.49.1:8080 from your web browser.");
                alert.setPositiveButton("Ok", null);
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    alert.show();
                }
            }
        }
    }
    class LoadData extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            assign(ConnectToPC.this);
            handler.post(new Runnable() {
                @Override
                public void run() {
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
                        Method.getDirFromRoot(FileItemFragment.fileList.get(0).toString(), ConnectToPC.this, FileItemFragment.fileList.get(0).toString());
                    }
                }
            });
            return null;
        }
    }
}

