package com.ctrlaltfix.indishare.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Models.AppModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.TrackDataModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;

import java.util.List;

public class TrackDataAdapter extends RecyclerView.Adapter<TrackDataAdapter.ViewHolder> {

    public static List<TrackDataModel> mValues;
    public static Context context;


    public TrackDataAdapter(List<TrackDataModel> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public TrackDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track_bottom_sheet_list_dialog_item, parent, false);
        return new TrackDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrackDataModel model = mValues.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (model.getType().equals("Audio")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.music_note,null));
            }else if (model.getType().equals("Videos")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_video_library_24,null));
            }else if (model.getType().equals("Images")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_image_24,null));
            }else if (model.getType().equals("APK")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_android_24,null));
            }else if (model.getType().equals("Documents")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24,null));
            }else if (model.getType().equals("Compressed")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_folder_24,null));
            }else {
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24,null));
            }
        }else{
            if (model.getType().equals("Audio")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.music_note));
            }else if (model.getType().equals("Videos")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_video_library_24));
            }else if (model.getType().equals("Images")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_image_24));
            }else if (model.getType().equals("APK")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_android_24));
            }else if (model.getType().equals("Documents")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24));
            }else if (model.getType().equals("Compressed")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_folder_24));
            }else {
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24));
            }
        }


        holder.name.setText(model.getName());

        holder.totalsize.setText(model.getTotalSize());

        holder.progress.setProgress(Constant.trackDataModels.get(position).getProgress());
        updateData(holder, position);

    }
    public void updateData(TrackDataAdapter.ViewHolder holder, final int position){
        if (Constant.trackDataModels.size()>position) {
            holder.size.setText(Constant.trackDataModels.get(position).getSizeInBytesString());
            holder.progress.setProgress(Constant.trackDataModels.get(position).getProgress());
            if (Constant.trackDataModels.get(position).getSizeInBytesLong() != Constant.trackDataModels.get(position).getTotalSizeInBytes() && Constant.trackDataModels.get(position).getProgress()!=100){
                refresh(1, holder, position);
            }else {
                holder.size.setText(Constant.trackDataModels.get(position).getFinal());
            }
        }
    }

    private void refresh(int millisecond, final TrackDataAdapter.ViewHolder holder, final int position){
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateData(holder, position);
            }
        };

        handler.postDelayed(runnable, millisecond);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView thumbnail;
        public ProgressBar progress;
        public TextView name;
        public TextView size;
        public TextView totalsize;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            thumbnail = mView.findViewById(R.id.thumbnail);
            progress = mView.findViewById(R.id.progress);
            name = mView.findViewById(R.id.name);
            size = mView.findViewById(R.id.size);
            totalsize = mView.findViewById(R.id.totalsize);
        }

        // @Override
        //public String toString() {
        //  return super.toString() + " '" + mContentView.getText() + "'";
        //}
    }
}
