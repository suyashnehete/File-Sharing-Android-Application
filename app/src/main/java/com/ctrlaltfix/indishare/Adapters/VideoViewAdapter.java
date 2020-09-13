package com.ctrlaltfix.indishare.Adapters;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Models.FileModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;

import java.io.File;
import java.util.List;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {

    private final List<FileModel> mValues;
    private final Context context;

    public VideoViewAdapter(List<FileModel> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FileModel fileModel = mValues.get(position);

        holder.title.setText(fileModel.getName());
        holder.size.setText(fileModel.getSize());
        holder.duration.setText(fileModel.getDuration());
        Glide.with(context).load(fileModel.getUri()).thumbnail(.1f).into(holder.thumbnail);
        final SendFileDetailsModel model = new SendFileDetailsModel(fileModel.getName(), fileModel.getSize(), "Videos", fileModel.getSizeInBytes());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.sendUri.contains(Uri.fromFile(fileModel.getFile()))) {
                    add(fileModel, model, holder);
                } else {
                    remove(fileModel, model, holder);
                }
            }
        });


        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(fileModel.getUri());
                intent.setFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION|
                                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION|
                                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION|
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                context.startActivity(intent);
                return true;
            }
        });

    }

    private void add(FileModel fileModel, final SendFileDetailsModel model, final VideoViewAdapter.ViewHolder holder){
        Constant.sendUri.add(Uri.fromFile(fileModel.getFile()));
        Constant.sendOriginalUri.add(fileModel.getUri());
        Constant.sendUriDetails.add(model);
        holder.selected.setImageDrawable(context.getResources().getDrawable(R.drawable.checked_image));
    }

    private void remove(FileModel fileModel, final SendFileDetailsModel model, final VideoViewAdapter.ViewHolder holder){
        Constant.sendUri.remove(Uri.fromFile(fileModel.getFile()));
        Constant.sendOriginalUri.remove(fileModel.getUri());
        Constant.sendUriDetails.remove(model);
        holder.selected.setImageDrawable(context.getResources().getDrawable(R.drawable.unchecked_image));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView title;
        public TextView size;
        public TextView duration;
        public ImageView thumbnail;
        public ImageView selected;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = mView.findViewById(R.id.title);
            size = mView.findViewById(R.id.size);
            thumbnail = mView.findViewById(R.id.thumbnail);
            selected = mView.findViewById(R.id.selected);
            duration = mView.findViewById(R.id.duration);
        }

       // @Override
        //public String toString() {
          //  return super.toString() + " '" + mContentView.getText() + "'";
        //}
    }
}