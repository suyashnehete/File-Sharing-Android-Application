package com.ctrlaltfix.indishare.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.Models.AppModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;

import java.util.List;

public class AppViewAdapter extends RecyclerView.Adapter<AppViewAdapter.ViewHolder> {

    private final List<AppModel> mValues;
    private final Context context;
    private final int width;


    public AppViewAdapter(List<AppModel> mValues, Context context, int width) {
        this.mValues = mValues;
        this.context = context;
        this.width = width;
    }

    @Override
    public AppViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apps_list, parent, false);
        view.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
        return new AppViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppViewAdapter.ViewHolder holder, final int position) {

        final AppModel appModel = mValues.get(position);

        Glide.with(context).load(appModel.getIcon()).into(holder.icon);
        holder.name.setText(appModel.getName());
        holder.size.setText(appModel.getSize());
        final SendFileDetailsModel model = new SendFileDetailsModel(appModel.getName()+".apk", appModel.getSize(), "APK", appModel.getSizeInBytes());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.sendUri.contains(Uri.fromFile(appModel.getFile()))) {
                    add(appModel, model, holder);
                } else {
                    remove(appModel, model, holder);
                }
            }
        });

    }

    private void add(AppModel appModel, final SendFileDetailsModel model, final AppViewAdapter.ViewHolder holder){
        Constant.sendUri.add(Uri.fromFile(appModel.getFile()));
        Constant.sendOriginalUri.add(Uri.fromFile(appModel.getFile()));
        Constant.sendUriDetails.add(model);
        holder.selected.setImageDrawable(context.getResources().getDrawable(R.drawable.checked_image));
    }

    private void remove(AppModel appModel, final SendFileDetailsModel model, final AppViewAdapter.ViewHolder holder){
        Constant.sendUri.remove(Uri.fromFile(appModel.getFile()));
        Constant.sendOriginalUri.remove(Uri.fromFile(appModel.getFile()));
        Constant.sendUriDetails.remove(model);
        holder.selected.setImageResource(0);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView icon;
        public ImageView selected;
        public TextView name;
        public TextView size;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            icon = mView.findViewById(R.id.icon);
            selected = mView.findViewById(R.id.selected);
            name = mView.findViewById(R.id.name);
            size = mView.findViewById(R.id.size);
        }

        // @Override
        //public String toString() {
        //  return super.toString() + " '" + mContentView.getText() + "'";
        //}
    }
}