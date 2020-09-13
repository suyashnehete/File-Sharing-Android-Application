package com.ctrlaltfix.indishare.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.ctrlaltfix.indishare.MainActivity;
import com.ctrlaltfix.indishare.SendReceiveActivity;
import com.ctrlaltfix.indishare.Utils.Constant;

public class SendRecevieBroadcastReceiver extends BroadcastReceiver {

    private WifiManager wifiManager;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private SendReceiveActivity activity;


    public SendRecevieBroadcastReceiver(WifiManager wifiManager, WifiP2pManager manager, WifiP2pManager.Channel channel, SendReceiveActivity activity) {
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
            if (state == WifiManager.WIFI_STATE_DISABLED){
                activity.disconnect();
                Constant.sendOriginalUri.clear();
                Constant.sendUri.clear();
                Constant.sendUriDetails.clear();
                Constant.address=null;
                Constant.adminAddress=null;
                Constant.isGroupOwner=null;
                Constant.trackDataIndex = 0;
                Constant.trackDataModels.clear();
            }
        }else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "Device Successfully Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Device Disconnected", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

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
                Constant.sendUri.clear();
                Constant.sendUriDetails.clear();
                Constant.address=null;
                Constant.adminAddress=null;
                Constant.isGroupOwner=null;
                activity.finishAffinity();
                activity.serverSocket = null;
                activity.server = null;
                context.startActivity(
                        new Intent(context, MainActivity.class)
                );
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }
    }
}

