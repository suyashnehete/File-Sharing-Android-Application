package com.ctrlaltfix.indishare.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.ArraySet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctrlaltfix.indishare.Adapters.TrackSendAdapter;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.Models.AppModel;
import com.ctrlaltfix.indishare.Models.FileModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.Models.TrackUserFileModel;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class Constant {

    //all loaded files will be here
    public static ArrayList<FileModel> allVideoListA_Z = new ArrayList<>();
    public static ArrayList<FileModel> allVideoListZ_A = new ArrayList<>();
    public static ArrayList<FileModel> allVideoListModificationTime = new ArrayList<>();
    public static ArrayList<FileModel> allVideoListLarge = new ArrayList<>();
    public static ArrayList<FileModel> allVideoListFirst = new ArrayList<>();

    public static ArrayList<FileModel> allAudioListA_Z = new ArrayList<>();
    public static ArrayList<FileModel> allAudioListZ_A = new ArrayList<>();
    public static ArrayList<FileModel> allAudioListModificationTime = new ArrayList<>();
    public static ArrayList<FileModel> allAudioListLarge = new ArrayList<>();
    public static ArrayList<FileModel> allAudioListFirst = new ArrayList<>();

    public static ArrayList<FileModel> allImageListA_Z = new ArrayList<>();
    public static ArrayList<FileModel> allImageListZ_A = new ArrayList<>();
    public static ArrayList<FileModel> allImageListModificationTime = new ArrayList<>();
    public static ArrayList<FileModel> allImageListLarge = new ArrayList<>();
    public static ArrayList<FileModel> allImageListFirst = new ArrayList<>();

    public static ArrayList<AppModel> appList = new ArrayList<>();

    public static ArrayList<Uri> sendUri = new ArrayList<>();
    public static ArrayList<Uri> sendOriginalUri = new ArrayList<>();
    public static ArrayList<SendFileDetailsModel> sendUriDetails = new ArrayList<>();
    public static ArrayList<SendFileDetailsModel> onlyForServer = new ArrayList<>();

    public static String address;

    public static InetAddress adminAddress;
    public static Boolean isGroupOwner;

    public static int constantSize;
    public static ArrayList<TrackDataModel> trackDataModels = new ArrayList<>();
    public static ArrayList<TrackUserFileModel> trackUserFileModels = new ArrayList<>();
    public static int trackDataIndex;
    public static int constantTrack;
    public static boolean isRunning;

    public static ArrayList<Uri> sendUsingSend = new ArrayList<>();
    public static ArrayList<Uri> sendUsingOriginalSend = new ArrayList<>();
    public static ArrayList<SendFileDetailsModel> sendAllUsingFiles = new ArrayList<>();


    public static ArrayList<ContactList> registeredContactsList = new ArrayList<>();
    public static ArrayList<String> phoneNumbers = new ArrayList<>();
    public static ArrayList<String> phoneNames = new ArrayList<>();
    public static ArrayList<String> firebaseNumbers = new ArrayList<>();
    public static ArrayList<String> firebaseNames = new ArrayList<>();
}

