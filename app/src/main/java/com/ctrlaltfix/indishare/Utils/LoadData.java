package com.ctrlaltfix.indishare.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.MediaStore;

public class LoadData extends AsyncTask {

    Context context;

    public LoadData(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Constant.appList = Method.getInstalledApps(context);
        Constant.allAudioListModificationTime = Method.getAudio(context, MediaStore.Audio.Media.DATE_MODIFIED + " DESC");
        Constant.allVideoListModificationTime = Method.getVideo(context, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        Constant.allImageListModificationTime = Method.getImages(context, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        Constant.allAudioListA_Z = Method.getAudio(context, MediaStore.Audio.Media.DISPLAY_NAME + " ASC");
        Constant.allVideoListA_Z = Method.getVideo(context, MediaStore.Video.Media.DISPLAY_NAME + " ASC");
        Constant.allImageListA_Z = Method.getImages(context, MediaStore.Images.Media.DISPLAY_NAME + " ASC");

        Constant.allAudioListZ_A = Method.getAudio(context, MediaStore.Audio.Media.DISPLAY_NAME + " DESC");
        Constant.allVideoListZ_A = Method.getVideo(context, MediaStore.Video.Media.DISPLAY_NAME + " DESC");
        Constant.allImageListZ_A = Method.getImages(context, MediaStore.Images.Media.DISPLAY_NAME + " DESC");

        Constant.allAudioListFirst = Method.getAudio(context, MediaStore.Audio.Media.SIZE + " ASC");
        Constant.allVideoListFirst = Method.getVideo(context, MediaStore.Video.Media.SIZE + " ASC");
        Constant.allImageListFirst = Method.getImages(context, MediaStore.Images.Media.SIZE + " ASC");

        Constant.allAudioListLarge = Method.getAudio(context, MediaStore.Audio.Media.SIZE + " DESC");
        Constant.allVideoListLarge = Method.getVideo(context, MediaStore.Video.Media.SIZE + " DESC");
        Constant.allImageListLarge = Method.getImages(context, MediaStore.Images.Media.SIZE + " DESC");
        return null;
    }
}
