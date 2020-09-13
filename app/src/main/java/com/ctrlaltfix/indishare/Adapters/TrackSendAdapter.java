package com.ctrlaltfix.indishare.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Fragments.TrackSendBottomSheetFragment;
import com.ctrlaltfix.indishare.Models.TrackUserFileModel;
import com.ctrlaltfix.indishare.Models.UserFileModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrackSendAdapter extends RecyclerView.Adapter<TrackSendAdapter.ViewHolder> {

    private final List<TrackUserFileModel> mValues;
    private final Context context;

    public TrackSendAdapter(List<TrackUserFileModel> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_receive_any_where, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrackUserFileModel userFileModel = mValues.get(position);
        holder.download.setVisibility(View.GONE);
        holder.open.setVisibility(View.GONE);
        holder.cancel.setVisibility(View.VISIBLE);
        Glide.with(context).load(userFileModel.getUri()).thumbnail(.1f).into(holder.thumbnail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (userFileModel.getType().equals("Audio")) {
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.music_note, null));
            }else if (userFileModel.getType().equals("APK")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_android_24,null));
            }else if (userFileModel.getType().equals("Documents")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24,null));
            }else if (userFileModel.getType().equals("Compressed")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_folder_24,null));
            }else {
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24,null));
            }
        }else{
            if (userFileModel.getType().equals("Audio")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.music_note));
            }else if (userFileModel.getType().equals("APK")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_android_24));
            }else if (userFileModel.getType().equals("Documents")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24));
            }else if (userFileModel.getType().equals("Compressed")){
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_folder_24));
            }else {
                holder.thumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_insert_drive_file_24));
            }
        }
        
        holder.title.setText(userFileModel.getName());
        holder.size.setText(Method.sizeConverter(userFileModel.getSize()));
        
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFileModel.getUploadTask() != null) {
                    userFileModel.getUploadTask().cancel();
                }
                Constant.trackUserFileModels.remove(position);
                TrackSendBottomSheetFragment.adapter.notifyDataSetChanged();
            }
        });

        holder.open.setText("Share");
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Share File")
                            .setMessage("Share the Share Code with your friend.\nShare Code: " + userFileModel.getShareCode())
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(
                                            Intent.createChooser(
                                                    new Intent(Intent.ACTION_SEND)
                                                            .setType("text/plain")
                                                            .putExtra(Intent.EXTRA_SUBJECT, "ShareCode of IndiShare SendAnyWhere")
                                                            .putExtra(Intent.EXTRA_TEXT, "Enter this code in your indishare SendAnyWhere App to download " + userFileModel.getName() + " file."),
                                                    "Share Using:-"
                                            )
                                    );
                                }
                            })
                            .show();
                }
            }
        });

        if (userFileModel.getUploadTask() != null){
            userFileModel.getUploadTask()
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    holder.cancel.setVisibility(View.GONE);
                    holder.open.setVisibility(View.VISIBLE);
                    holder.receiveSize.setText("Uploaded");
                    holder.progress.setProgress(100);
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            holder.receiveSize.setText(Method.sizeConverter(taskSnapshot.getBytesTransferred()));
                            holder.progress.setProgress((int)(((float)taskSnapshot.getBytesTransferred()/(float)taskSnapshot.getTotalByteCount())*100));
                        }
                    })
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    holder.cancel.setVisibility(View.GONE);
                    holder.receiveSize.setText("Canceled");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.cancel.setVisibility(View.GONE);
                    holder.receiveSize.setText("Failed");
                }
            });
        }

    }
            @Override
            public int getItemCount() {
                return mValues.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public View mView;
                public TextView title;
                public TextView size;
                public TextView receiveSize;
                public ImageView thumbnail;
                public Button download;
                public Button open;
                public Button cancel;
                public ProgressBar progress;

                public ViewHolder(View view) {
                    super(view);
                    mView = view;
                    title = mView.findViewById(R.id.title);
                    size = mView.findViewById(R.id.size);
                    thumbnail = mView.findViewById(R.id.thumbnail);
                    download = mView.findViewById(R.id.download);
                    receiveSize = mView.findViewById(R.id.receiveSize);
                    cancel = mView.findViewById(R.id.cancel);
                    open = mView.findViewById(R.id.open);
                    progress = mView.findViewById(R.id.progress);
                }

                // @Override
                //public String toString() {
                //  return super.toString() + " '" + mContentView.getText() + "'";
                //}
            }
        }