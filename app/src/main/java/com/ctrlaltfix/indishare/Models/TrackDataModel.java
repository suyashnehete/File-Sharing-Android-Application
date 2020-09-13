package com.ctrlaltfix.indishare.Models;

import android.net.Uri;

import com.ctrlaltfix.indishare.Utils.Constant;

public class TrackDataModel {
    String name;
    String sizeInBytesString;
    long sizeInBytesLong=0;
    String totalSize;
    long totalSizeInBytes;
    Uri uri;
    int progress;
    String type;
    String Final;

    public TrackDataModel(String name, long sizeInBytes, String totalSize, Uri uri, int progress, String type, long totalSizeInBytes, String Final) {
        this.name = name;
        this.totalSize = totalSize;
        this.uri = uri;
        this.progress = progress;
        this.type = type;
        this.sizeInBytesLong = sizeInBytes;
        this.totalSizeInBytes = totalSizeInBytes;
        this.Final = Final;
    }

    public String getFinal() {
        return Final;
    }

    public void setFinal(String aFinal) {
        Final = aFinal;
    }

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public void setTotalSizeInBytes(long totalSizeInBytes) {
        this.totalSizeInBytes = totalSizeInBytes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSizeInBytesString() {
        if (sizeInBytesLong > (1024 * 1024 * 1024)) {
            return String.format("%.2f", (double) sizeInBytesLong / (1024 * 1024 * 1024)) + " GB";
        }else if (sizeInBytesLong > (1024 * 1024)) {
            return String.format("%.2f", (double) sizeInBytesLong / (1024 * 1024)) + " MB";
        }else if (sizeInBytesLong > (1024)) {
            return String.format("%.2f", (double) sizeInBytesLong / (1024)) + " KB";
        }else{
            return String.format("%.2f", (double) sizeInBytesLong) + " B";
        }
    }

    public void setSizeInBytesString(String sizeInBytesString) {
        this.sizeInBytesString = sizeInBytesString;
    }

    public long getSizeInBytesLong() {
        return sizeInBytesLong;
    }

    public void setSizeInBytesLong(long sizeInBytesLong) {
        this.sizeInBytesLong += sizeInBytesLong;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
