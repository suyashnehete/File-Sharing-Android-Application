
package com.ctrlaltfix.indishare.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;


import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlaltfix.indishare.Adapters.FileViewAdapter;
import com.ctrlaltfix.indishare.Fragments.FileItemFragment;
import com.ctrlaltfix.indishare.Fragments.TrackBottomSheetFragment;
import com.ctrlaltfix.indishare.Models.AppModel;
import com.ctrlaltfix.indishare.Models.FileModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.SendActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Looper.getMainLooper;

public class Method {

    public static String calculateNoOfColumns(Context context, int max) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = 1;
        int width;
        while (true){
            width =  (int) screenWidthDp/noOfColumns;
            if (width <= max){
                width*=displayMetrics.density;
                break;
            }
            noOfColumns++;
        }
        return noOfColumns+"<>"+width;
    }



    public static ArrayList<FileModel> getVideo(Context context, String sortOrder){
        ArrayList<FileModel> allVideoList = new ArrayList<>();
        try {

            final String[] columns = {
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE,
            };
            MergeCursor cursor = new MergeCursor(new Cursor[]{
                    context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder),
                    context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null, null, sortOrder)
            });
            cursor.moveToFirst();
            do{
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                long duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int lastPoint = path.lastIndexOf(".");
                path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                allVideoList.add(new FileModel(new File(path), contentUri, duration, size, context, name, date));
            }while (cursor.moveToNext());
        } catch (Exception e) {
        }

        return allVideoList;
    }

    public static ArrayList<FileModel> getAudio(Context context, String sortOrder){
        ArrayList<FileModel> allVideoList = new ArrayList<>();
        try {

            final String[] columns = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_MODIFIED,
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.SIZE,
            };
            MergeCursor cursor = new MergeCursor(new Cursor[]{
                    context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder),
                    context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, columns, null, null, sortOrder)
            });
            cursor.moveToFirst();
            do{
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                long duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                int lastPoint = path.lastIndexOf(".");
                path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                allVideoList.add(new FileModel(new File(path), contentUri, duration, size, context, name, date));
            }while (cursor.moveToNext());
        } catch (Exception e) {
        }

        return allVideoList;
    }

    public static ArrayList<FileModel> getImages(Context context, String sortOrder){
        ArrayList<FileModel> allVideoList = new ArrayList<>();
        try {

            final String[] columns = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
            };
            MergeCursor cursor = new MergeCursor(new Cursor[]{
                    context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder),
                    context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, sortOrder)
            });
            cursor.moveToFirst();
            do{
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                long size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                int lastPoint = path.lastIndexOf(".");
                path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                allVideoList.add(new FileModel(new File(path), contentUri, (long)0, size, context, name, date));
            }while (cursor.moveToNext());
        } catch (Exception e) {
        }

        return allVideoList;
    }

    public static ArrayList<AppModel> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        ArrayList<AppModel> app = new ArrayList<>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(context.getPackageManager());
                String packages = p.applicationInfo.packageName;
                String file = p.applicationInfo.publicSourceDir;

                app.add(new AppModel(appName, packages, new File(file), icon, new File(file).length()));
            }
        }
        return app;
    }

    private static boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static void run(int len, int index) {
        if (Constant.trackDataModels.size() != 0 && Constant.trackDataModels.size() > index) {
            Constant.trackDataModels.get(index).setSizeInBytesLong(len);
            Constant.trackDataModels.get(index).setProgress((int)(((float)Constant.trackDataModels.get(index).getSizeInBytesLong()/(float)Constant.trackDataModels.get(index).getTotalSizeInBytes()) *100));
        }
    }

    public static File getFile(Context context, String type, String name){
        if (type.equals("temp")){
            return new File(context.getExternalFilesDir("received"), name);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new File("/storage/emulated/0/IndiShare/"+type+"/", name);
        }else{
            return new File(Environment.getExternalStoragePublicDirectory("IndiShare/"+type), name);
        }
    }

    public static void getDirFromRoot(String rootPath, Context context, String root)
    {
        ArrayList<String> item = new ArrayList<String>();
        Boolean isRoot=true;
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> files=new ArrayList<String>();
        ArrayList<String> filesPath=new ArrayList<String>();
        File m_file = new File(rootPath);
        File[] filesArray = m_file.listFiles();
        if(!rootPath.equals(root))
        {
            item.add("../");
            path.add(m_file.getParent());
            isRoot=false;
        }
        String curDir=rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(filesArray);
        for(int i=0; i < filesArray.length; i++)
        {
            File file = filesArray[i];
            if(file.isDirectory())
            {
                item.add(file.getName());
                path.add(file.getPath());
            }
            else
            {
                files.add(file.getName());
                filesPath.add(file.getPath());
            }
        }
        for(String m_AddFile:files)
        {
            item.add(m_AddFile);
        }
        for(String m_AddPath:filesPath)
        {
            path.add(m_AddPath);
        }
        RecyclerView.Adapter adapter = new FileViewAdapter(item,path,context,isRoot);
        FileItemFragment.recyclerView.setAdapter(adapter);
    }

    public static void setDeviceName(String name, Context context){
        try {
            WifiP2pManager manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
            WifiP2pManager.Channel channel = manager.initialize(context, getMainLooper(), null);
            java.lang.reflect.Method m = manager.getClass().getMethod("setDeviceName", new Class[]{channel.getClass(), String.class,
                    WifiP2pManager.ActionListener.class});
            m.invoke(manager, channel, name, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("IndiShareClient",context.MODE_PRIVATE);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getType(String name) {
        String type = "Other";
        if (name.contains(".3g2") || name.contains(".3gp") || name.contains(".avi") || name.contains(".flv") || name.contains(".h264") || name.contains(".m4v") || name.contains(".mkv") || name.contains(".mov") || name.contains(".mp4") || name.contains(".mpeg") || name.contains(".mpg") || name.contains(".rm") || name.contains(".swf") || name.contains(".vob") || name.contains(".wmv")){
            type = "Videos";
        }else if(name.contains(".ai") || name.contains(".bmp") || name.contains(".gif") || name.contains(".ico") || name.contains(".jpeg") || name.contains(".jpg") || name.contains(".png") || name.contains(".ps") || name.contains(".psd") || name.contains(".svg") || name.contains(".tif") || name.contains(".tiff")){
            type = "Images";
        }else if(name.contains(".aif") || name.contains(".cda") || name.contains(".mid") || name.contains(".midi") || name.contains(".mp3") || name.contains(".mpa") || name.contains(".ogg") || name.contains(".wav") || name.contains(".wma") || name.contains(".wpl")){
            type = "Audio";
        }else if(name.contains(".apk")){
            type = "APK";
        }else if(name.contains(".doc") || name.contains(".docx") || name.contains(".odt") || name.contains(".pdf") || name.contains(".rtf") || name.contains(".tex") || name.contains(".txt") || name.contains(".wpd") || name.contains(".key") || name.contains(".odp") || name.contains(".pps") || name.contains(".ppt") || name.contains(".pptx") || name.contains(".ods") || name.contains(".xls") || name.contains(".xlsm") || name.contains(".xlsx")){
            type = "Documents";
        }else if(name.contains(".7z") || name.contains(".arj") || name.contains(".deb") || name.contains(".pkg") || name.contains(".rar") || name.contains(".rpm") || name.contains(".tar") || name.contains(".gz") || name.contains(".z") || name.contains(".zip")){
            type = "Compressed";
        }
        return type;
    }

    public static String sizeConverter(Long size){
        if (size > (1024 * 1024 * 1024)) {
            return String.format("%.2f", (double) size / (1024 * 1024 * 1024)) + " GB";
        }else if (size > (1024 * 1024)) {
            return String.format("%.2f", (double) size / (1024 * 1024)) + " MB";
        }else if (size > (1024)) {
            return String.format("%.2f", (double) size / (1024)) + " KB";
        }else {
            return String.format("%.2f", (double) size) + " B";
        }
    }

    public static void handleSendText(Intent intent, Context context) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            try {
                File f = new File(context.getExternalCacheDir(), "file.txt");
                f.createNewFile();
                FileWriter writer = new FileWriter(f);
                writer.append(sharedText);
                writer.flush();
                writer.close();

                Uri uri = Uri.fromFile(f);

                Constant.sendUsingSend.add(uri);
                Constant.sendUsingOriginalSend.add(uri);
                Constant.sendAllUsingFiles.add(new SendFileDetailsModel(f.getName(), Method.sizeConverter(f.length()), "Documents", f.length()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleSendImage(Intent intent, Context context) {
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            File f = new File(Method.getRealPathFromURI(context , uri));
            Constant.sendUsingSend.add(Uri.fromFile(f));
            Constant.sendUsingOriginalSend.add(uri);
            Constant.sendAllUsingFiles.add(new SendFileDetailsModel(f.getName(), Method.sizeConverter(f.length()), Method.getType(f.getName()), f.length()));
        }
    }

    public static void handleSendMultipleImages(Intent intent, Context context) {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            for (Uri uri : uris){
                File f = new File(Method.getRealPathFromURI(context , uri));
                Constant.sendUsingSend.add(Uri.fromFile(f));
                Constant.sendUsingOriginalSend.add(uri);
                Constant.sendAllUsingFiles.add(new SendFileDetailsModel(f.getName(), Method.sizeConverter(f.length()), Method.getType(f.getName()), f.length()));
            }
        }
    }
    public static void handleSendMultipleText(Intent intent, Context context) {
        ArrayList<? extends String> sharedText = intent.getParcelableArrayListExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            try {
                File f = new File(context.getExternalCacheDir(), "file.txt");
                f.createNewFile();
                FileWriter writer = new FileWriter(f);
                for (String s : sharedText) {
                    writer.append(s);
                    writer.flush();
                    writer.close();
                }
                Uri uri = Uri.fromFile(f);

                Constant.sendUsingSend.add(uri);
                Constant.sendUsingOriginalSend.add(uri);
                Constant.sendAllUsingFiles.add(new SendFileDetailsModel(f.getName(), Method.sizeConverter(f.length()), "Documents", f.length()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int get70Percent(Context context){
        float density = context.getResources().getDisplayMetrics().widthPixels;
        int division = (int) density/100;
        int result = division*80;
        return result;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static int getDrawable(String name){

        String[] temp = name.split(".");
        String extension = temp.length > 0 ? temp[temp.length-1] : "";

        if (extension.equals("pdf")){
            return R.drawable.ic_baseline_edit_24;
        }else if(extension.equals("odt") || extension.equals("ott") || extension.equals("fodt") || extension.equals("uot") || extension.equals("docx") || extension.equals("dotx") || extension.equals("doc") || extension.equals("rtx") || extension.equals("dot") || extension.equals("docm")){
            return R.drawable.ic_baseline_edit_24;
        }else if(extension.equals("odp") || extension.equals("otp") || extension.equals("odg") || extension.equals("fodp") || extension.equals("uop") || extension.equals("pptx") || extension.equals("ppsx") || extension.equals("potx") || extension.equals("ppt") || extension.equals("pps") || extension.equals("pot") || extension.equals("pptx") || extension.equals("ppsx") || extension.equals("potx") || extension.equals("pptm")){
            return R.drawable.ic_baseline_edit_24;
        }else if(extension.equals("fods") || extension.equals("ods") || extension.equals("ots") || extension.equals("fods") || extension.equals("uos") || extension.equals("xltx") || extension.equals("xls") || extension.equals("xlt") || extension.equals("dif") || extension.equals("dbf") || extension.equals("slk") || extension.equals("csv") || extension.equals("xlsx") || extension.equals("xlsm")){
            return R.drawable.ic_baseline_edit_24;
        }else if(extension.equals("apk") || extension.equals(".exe") || extension.equals(".deb")){
            return R.drawable.ic_baseline_edit_24;
        }else{
            return R.drawable.ic_baseline_edit_24;
        }
    }
}
