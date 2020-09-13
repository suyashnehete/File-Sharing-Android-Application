package com.ctrlaltfix.indishare.Models;

import java.io.Serializable;

public class SendFileDetailsModel implements Serializable {

    String name;
    String size;
    String type;
    long sizeInBytes;

    public SendFileDetailsModel(String name, String size, String type, long sizeInBytes) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.sizeInBytes = sizeInBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
}
