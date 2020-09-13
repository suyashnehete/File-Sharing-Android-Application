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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Models.FileModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.Models.UserFileModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class SendAnywhereAdapter extends RecyclerView.Adapter<SendAnywhereAdapter.ViewHolder> {

    private final List<UserFileModel> mValues;
    private final Context context;

    public SendAnywhereAdapter(List<UserFileModel> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_send_any_where, parent, false);
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


        holder.title.setText(userFileModel.getName());
        holder.size.setText(Method.sizeConverter(userFileModel.getSize()));
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        Intent.createChooser(
                                new Intent(Intent.ACTION_SEND, Uri.parse(userFileModel.getUri())),
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
                public ImageView thumbnail;
                public Button open;

                public ViewHolder(View view) {
                    super(view);
                    mView = view;
                    title = mView.findViewById(R.id.title);
                    size = mView.findViewById(R.id.size);
                    thumbnail = mView.findViewById(R.id.thumbnail);
                    open = mView.findViewById(R.id.open);
                }

                // @Override
                //public String toString() {
                //  return super.toString() + " '" + mContentView.getText() + "'";
                //}
            }
        }