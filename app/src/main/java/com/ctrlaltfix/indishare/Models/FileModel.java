package com.ctrlaltfix.indishare.Models;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.core.content.FileProvider;

import com.ctrlaltfix.indishare.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileModel {
    File file;
    Uri uri;
    String duration;
    String size;
    Context context;
    String name;
    String dateModified;
    long sizeInBytes;

    public FileModel(File file, Uri uri, Long duration, Long size, Context context, String name, Long dateModified) {
        this.file = file;
        this.uri = uri;
        this.duration = convertMillieToHMmSs(duration);
        this.dateModified = convertMillieToHMmSs(dateModified);
        this.context = context;
        this.name = name;
        this.sizeInBytes = size;

        if (size > (1024 * 1024 * 1024)) {
            this.size = String.format("%.2f", (double) size / (1024 * 1024 * 1024)) + " GB";
        }else if (size > (1024 * 1024)) {
            this.size = String.format("%.2f", (double) size / (1024 * 1024)) + " MB";
        }else if (size > (1024)) {
            this.size = String.format("%.2f", (double) size / (1024)) + " KB";
        }else {
            this.size = String.format("%.2f", (double) size) + " B";
        }

    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else {
            return String.format("%02d:%02d" , minute, second);
        }
    }



}
