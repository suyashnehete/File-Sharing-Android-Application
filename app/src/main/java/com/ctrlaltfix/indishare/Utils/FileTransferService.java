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

public class FileTransferService implements Runnable{
    Socket socket;
    Context context;

    public FileTransferService(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            while(Constant.sendUri.size() > Constant.trackDataIndex) {
                socket = new Socket();
                if (Constant.isGroupOwner) {
                    socket.connect((new InetSocketAddress(Constant.address, 8988)), 50000);
                } else {
                    socket.connect((new InetSocketAddress(Constant.adminAddress, 8888)), 50000);
                }
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                int index = 0;

                for(int i = 0; i< Constant.trackDataModels.size(); i++){
                    if (Constant.trackDataModels.get(i).getName().equals(Constant.sendUriDetails.get(Constant.trackDataIndex).getName())  && Constant.trackDataModels.get(i).getProgress() == 0){
                        index = i;
                        break;
                    }
                }
                try {
                    is = cr.openInputStream(Constant.sendUri.get(Constant.trackDataIndex));
                } catch (FileNotFoundException e) {
                }
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) != -1) {
                    stream.write(buf, 0, len);
                    Method.run(len, index);
                }

                stream.close();
                is.close();
                socket.close();
                Constant.trackDataIndex++;
            }
            Constant.isRunning = false;
            Constant.trackDataIndex = 0;
            Constant.sendUri.clear();
            Constant.sendOriginalUri.clear();
            Constant.sendUriDetails.clear();
        } catch (IOException e) {
        }

    }
}
