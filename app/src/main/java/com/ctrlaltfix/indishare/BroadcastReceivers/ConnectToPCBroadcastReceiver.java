package com.ctrlaltfix.indishare.BroadcastReceivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.core.app.ActivityCompat;

import com.ctrlaltfix.indishare.ConnectToPC;
import com.ctrlaltfix.indishare.SendActivity;

public class ConnectToPCBroadcastReceiver extends BroadcastReceiver {

    private WifiManager wifiManager;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ConnectToPC activity;

    public ConnectToPCBroadcastReceiver(WifiManager wifiManager, WifiP2pManager manager, WifiP2pManager.Channel channel, ConnectToPC activity) {
        this.wifiManager = wifiManager;
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiManager.WIFI_STATE_ENABLED) {
                activity.wifiManager.setWifiEnabled(true);
                activity.discover();
            } else if (state == WifiManager.WIFI_STATE_DISABLED) {
                activity.discover();
            }
        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
                activity.connect();
                activity.startServer(activity.PORT);
            } else {
                activity.setIsWifiP2pEnabled(false);

            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.requestGroupInfo(channel, activity);
        }
    }
}
