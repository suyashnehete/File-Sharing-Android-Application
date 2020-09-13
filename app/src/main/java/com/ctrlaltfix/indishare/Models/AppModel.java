package com.ctrlaltfix.indishare.Models;

import android.graphics.drawable.Drawable;

import java.io.File;

public class AppModel {

    String name;
    String packagename;
    File file;
    Drawable icon;
    String size;
    long sizeInBytes;

    public AppModel() {
    }

    public AppModel(String name, String packagename, File file, Drawable icon, long size) {
        this.name = name;
        this.packagename = packagename;
        this.file = file;
        this.icon = icon;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
