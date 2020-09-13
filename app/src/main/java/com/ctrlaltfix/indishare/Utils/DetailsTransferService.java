package com.ctrlaltfix.indishare.Utils;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ctrlaltfix.indishare.SendReceiveActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.ctrlaltfix.indishare.Utils.Method.run;

public class DetailsTransferService implements Runnable{
    Socket filename;
    Context context;

    public DetailsTransferService(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("suyash", "entering");

        Log.d("suyash", "connecting");
        try {
            filename = new Socket();
            if (Constant.isGroupOwner!=null && Constant.isGroupOwner) {
                filename.connect((new InetSocketAddress(Constant.address, 8998)), 50000);
            } else {
                filename.connect((new InetSocketAddress(Constant.adminAddress, 8999)), 50000);
            }

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(filename.getOutputStream());
            objectOutputStream.writeObject(Constant.onlyForServer);
            objectOutputStream.close();
            filename.close();
            Log.d("suyash", "FTS1");
            if (!Constant.isRunning){
                Log.d("suyash", "FTS2");
                Constant.isRunning = true;
                Thread thread = new Thread(new FileTransferService(context));
                thread.start();
            }
        } catch (IOException e) {
            Log.e("FTP", e.getMessage());
        }


    }
}