package com.ctrlaltfix.indishare.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.ctrlaltfix.indishare.Models.UserFileModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReceiveAnywhereAdapter extends RecyclerView.Adapter<ReceiveAnywhereAdapter.ViewHolder> {

    private final List<UserFileModel> mValues;
    private final Context context;

    public ReceiveAnywhereAdapter(List<UserFileModel> mValues, Context context) {
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
        final UserFileModel userFileModel = mValues.get(position);
        Glide.with(context).load(userFileModel.getUrl()).thumbnail(.1f).into(holder.thumbnail);
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
        final FileDownloadTask[] downloadTask = new FileDownloadTask[1];
        final File[] file = new File[1];
        holder.title.setText(userFileModel.getName());
        holder.size.setText(Method.sizeConverter(userFileModel.getSize()));
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.download.setVisibility(View.GONE);
                holder.open.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
                file[0] = Method.getFile(context, userFileModel.getType(), userFileModel.getName());
                File dirs = new File(file[0].getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                try {
                    file[0].createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downloadTask[0] = FirebaseStorage.getInstance().getReferenceFromUrl(userFileModel.getUrl()).getFile(file[0]);
                downloadTask[0]
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                holder.download.setVisibility(View.GONE);
                                holder.open.setVisibility(View.VISIBLE);
                                holder.cancel.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.download.setVisibility(View.VISIBLE);
                                holder.open.setVisibility(View.GONE);
                                holder.cancel.setVisibility(View.GONE);
                                file[0].delete();
                            }
                        })
                        .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                holder.download.setVisibility(View.VISIBLE);
                                holder.open.setVisibility(View.GONE);
                                holder.cancel.setVisibility(View.GONE);
                                file[0].delete();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                                holder.receiveSize.setText(Method.sizeConverter(taskSnapshot.getBytesTransferred()));
                                holder.progress.setProgress((int)((float)((float)taskSnapshot.getBytesTransferred()/(float)taskSnapshot.getTotalByteCount())*100));
                            }
                        });
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask[0].cancel();
            }
        });

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(context, "com.ctrlaltfix.indishare.fileprovider", file[0]);
                Toast.makeText(context, "Opening...", Toast.LENGTH_SHORT).show();
                context.startActivity(
                        Intent.createChooser(
                                new Intent(Intent.ACTION_VIEW, uri),
                                "Open Using:-"
                        )
                );
            }
        });
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