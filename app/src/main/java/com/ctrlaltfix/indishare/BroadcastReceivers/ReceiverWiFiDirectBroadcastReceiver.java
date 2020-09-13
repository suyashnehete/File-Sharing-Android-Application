
package com.ctrlaltfix.indishare.BroadcastReceivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.ReceiveActivity;
import com.ctrlaltfix.indishare.Utils.Method;


/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class ReceiverWiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiManager wifiManager;
    private WifiP2pManager manager;
    private Channel channel;
    private ReceiveActivity activity;

    public ReceiverWiFiDirectBroadcastReceiver(WifiManager wifiManager, WifiP2pManager manager, Channel channel, ReceiveActivity activity) {
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
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiManager.WIFI_STATE_ENABLED){
                activity.wifiManager.setWifiEnabled(true);
                String str = Method.getSharedPreferences(activity).getString(activity.getResources().getString(R.string.app_name), null);
                if (str == null){
                    Method.setDeviceName(Build.BRAND+" "+Build.MODEL, activity);
                    Method.getSharedPreferences(activity).edit().putString(activity.getResources().getString(R.string.app_name), Build.BRAND+" "+Build.MODEL).commit();
                }else{
                    Method.setDeviceName(str, context);
                }
                activity.discover();
            }else if (state == WifiManager.WIFI_STATE_DISABLED){
                activity.disconnect();
            }
        }else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);

            } else {
                activity.setIsWifiP2pEnabled(false);

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.requestPeers(channel, activity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, activity);
            } else {
                // It's a disconnect
                activity.disconnect();
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            activity.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }
}
