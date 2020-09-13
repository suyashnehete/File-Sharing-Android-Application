package com.ctrlaltfix.indishare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.BroadcastReceivers.SenderWiFiDirectBroadcastReceiver;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.SendUI.RippleBackground;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.DeviceActionListener;
import com.ctrlaltfix.indishare.Utils.LoadData;
import com.ctrlaltfix.indishare.Utils.Method;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SendActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener, WifiP2pManager.ConnectionInfoListener {

    final static int WIFI_REQUEST_CODE = 200;
    public WifiManager wifiManager;

    public TextView devicename;
    public TextView initializing;

    int chances = 2;

    private static final String TAG = "Suyash";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

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
            return false;
        }

        // Hardware capability check
        if (wifiManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!wifiManager.isP2pSupported()) {
                return false;
            }
        }


        if (manager == null) {
            return false;
        }

        if (channel == null) {
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        new LoadData(this).execute();
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        initialization();
        startBroadcast();
        if (!wifiManager.isWifiEnabled()){
            turnONWiFi();
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.d("suyash", true+"InSending");
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.d("suyash", true+"");
            if ("text/plain".equals(type)) {
                Method.handleSendText(intent, this); // Handle text being sent
            } else {
                Method.handleSendImage(intent, this); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            Log.d("suyash", true+"");
            if ("text/plain".equals(type)) {
                Method.handleSendMultipleText(intent, this); // Handle multiple images being sent
            }else{
                Method.handleSendMultipleImages(intent, this); // Handle multiple images being sent
            }
        }

    }


    public void startBroadcast(){
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

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

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
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

    public void discover(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(SendActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(SendActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
                discover();
            }
        });
    }
    
    private void turnONWiFi() {
        // check version code for enabling wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendActivity.this);
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

        }else{
            if (!wifiManager.isWifiEnabled()){
                if (wifiManager.setWifiEnabled(true)){
                    chances = 2;
                }else{
                    chances--;
                    if (chances!=0){
                        turnONWiFi();
                    }else{
                        onBackPressed();
                    }
                }
            }
        }
    }

    private void initialization() {
        devicename = findViewById(R.id.devicename);
        initializing = findViewById(R.id.initializing);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WIFI_REQUEST_CODE){
            if (!wifiManager.isWifiEnabled()){
                turnONWiFi();
            }
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new SenderWiFiDirectBroadcastReceiver(wifiManager, manager, channel, SendActivity.this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {

    }

    @Override
    public void connect(WifiP2pConfig config) {

    }

    @Override
    public void disconnect() {
        //Constant.sendAllUsingFiles.clear();
        //Constant.sendUsingSend.clear();
        //Constant.sendUsingOriginalSend.clear();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {

            }

            @Override
            public void onSuccess() {
                manager = null;
            }

        });
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                ArrayList<WifiP2pDevice> wifiP2pDevices = new ArrayList<>();
                wifiP2pDevices.addAll(wifiP2pGroup.getClientList());
                String name;
                    if (info.isGroupOwner) {
                        name = wifiP2pDevices.get(0).deviceName;
                    } else {
                        name = wifiP2pGroup.getOwner().deviceName;
                    }
                    Intent i = new Intent(SendActivity.this, SendReceiveActivity.class);
                    i.putExtra("info", info);
                    i.putExtra("name", name);
                    startActivity(i);
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            retryChannel = true;
            manager.initialize(SendActivity.this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disconnect();
        finishAffinity();
        startActivity(
                new Intent(SendActivity.this, MainActivity.class)
        );
    }
}