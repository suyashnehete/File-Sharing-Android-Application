package com.ctrlaltfix.indishare.ChatSection.Adapters;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.Models.MessageModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Method;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/*public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.ViewHolder> {

    private final ArrayList<MessageModel> mValues;
    private final Context context;
    private final FirebaseUser mUser;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    private final boolean isGroup;
    private final static int LEFT_ITEM = 1;
    private final static int RIGHT_ITEM = 0;

    public MessageViewAdapter(ArrayList<MessageModel> items, Context context, DatabaseReference databaseReference, StorageReference storageReference, boolean isGroup) {
        mValues = items;
        this.context = context;
        this.databaseReference = databaseReference;
        this.storageReference = storageReference;
        this.isGroup = isGroup;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d("suyash", viewType+"");
        if (viewType == LEFT_ITEM){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_chat_left, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_chat_right, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MessageModel model = mValues.get(position);
        if (isGroup){
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(model.getName());
        }else{
            holder.name.setVisibility(View.GONE);
        }
        if (model.getUrl() == null){
            holder.image.setVisibility(View.GONE);
            holder.imageName.setVisibility(View.GONE);
        }else{
            if (model.getType().equals("img") || model.getType().equals("video")) {
                holder.image.setVisibility(View.VISIBLE);
                if (model.getSenderId().equals(mUser.getUid())) {
                    Glide.with(context).load(model.getUri()).thumbnail(.1f).into(holder.image);
                } else {
                    Glide.with(context).load(model.getUrl()).thumbnail(.1f).into(holder.image);
                }
            }else{
                holder.imageName.setVisibility(View.VISIBLE);
                holder.imageNameText.setText(model.getName());
                holder.imageNameIcon.setImageDrawable(context.getResources().getDrawable(Method.getDrawable(model.getName())));
            }
        }

        if (model.isDeleted()){
            if (model.getSenderId().equals(mUser.getUid())) {
                holder.msg.setText("You Deleted This Message");
            }else{
                holder.msg.setText("This message was deleted");
            }
            holder.date.setVisibility(View.GONE);
        }else{
            if (model.isDeletedByReceiver() || model.isDeletedBySender()){
                holder.msg.setText("You Deleted This Message");
                holder.date.setVisibility(View.GONE);
            }else if (model.getMessage() != null && !TextUtils.isEmpty(model.getMessage())){
                holder.msg.setText(model.getMessage());
                holder.date.setText(model.getDate()+" "+model.getTime());
            }else{
                holder.date.setText(model.getDate()+" "+model.getTime());
            }
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position).getSenderId().equals(mUser.getUid())){
            return RIGHT_ITEM;
        }else{
            return LEFT_ITEM;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RelativeLayout rl;
        public final LinearLayout imageName;
        public final ImageView image;
        public final ImageView imageNameIcon;
        public final TextView msg;
        public final TextView imageNameText;
        public final TextView date;
        public final TextView name;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            rl = view.findViewById(R.id.rl);
            image = view.findViewById(R.id.image);
            msg = view.findViewById(R.id.msg);
            date = view.findViewById(R.id.date);
            imageName = view.findViewById(R.id.imagename);
            imageNameIcon = view.findViewById(R.id.imagenameicon);
            imageNameText = view.findViewById(R.id.imagenametext);
            name = view.findViewById(R.id.name);

            rl.setLayoutParams(new RelativeLayout.LayoutParams(
                    Method.get70Percent(context),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

    }
}*/

public class MessageViewAdapter extends BaseAdapter {

    private final ArrayList<MessageModel> mValues;
    private final Context context;
    private final FirebaseUser mUser;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    private final boolean isGroup;
    private final static int LEFT_ITEM = 1;
    private final static int RIGHT_ITEM = 0;

    public MessageViewAdapter(ArrayList<MessageModel> items, Context context, DatabaseReference databaseReference, StorageReference storageReference, boolean isGroup) {
        mValues = items;
        this.context = context;
        this.databaseReference = databaseReference;
        this.storageReference = storageReference;
        this.isGroup = isGroup;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(mValues.get(position).getSenderId().equals(mUser.getUid())){
            return RIGHT_ITEM;
        }else{
            return LEFT_ITEM;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (getItemId(position) == RIGHT_ITEM) {
            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_chat_right, parent, false));
        }else{
            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_chat_left, parent, false));
        }

        final MessageModel model = mValues.get(position);
        if (isGroup){
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(model.getName());
        }else{
            holder.name.setVisibility(View.GONE);
        }
        if (model.getUrl() == null){
            holder.image.setVisibility(View.GONE);
            holder.imageName.setVisibility(View.GONE);
        }else{
            if (model.getType().equals("img") || model.getType().equals("video")) {
                holder.image.setVisibility(View.VISIBLE);
                if (model.getSenderId().equals(mUser.getUid())) {
                    Glide.with(context).load(model.getUri()).thumbnail(.1f).into(holder.image);
                } else {
                    Glide.with(context).load(model.getUrl()).thumbnail(.1f).into(holder.image);
                }
            }else{
                holder.imageName.setVisibility(View.VISIBLE);
                holder.imageNameText.setText(model.getName());
                holder.imageNameIcon.setImageDrawable(context.getResources().getDrawable(Method.getDrawable(model.getName())));
            }
        }

        if (model.isDeleted()){
            if (model.getSenderId().equals(mUser.getUid())) {
                holder.msg.setText("You Deleted This Message");
            }else{
                holder.msg.setText("This message was deleted");
            }
            holder.date.setVisibility(View.GONE);
        }else{
            if (model.isDeletedByReceiver() || model.isDeletedBySender()){
                holder.msg.setText("You Deleted This Message");
                holder.date.setVisibility(View.GONE);
            }else if (model.getMessage() != null && !TextUtils.isEmpty(model.getMessage())){
                holder.msg.setText(model.getMessage());
                holder.date.setText(model.getDate()+" "+model.getTime());
            }else{
                holder.date.setText(model.getDate()+" "+model.getTime());
            }
        }

        return holder.mView;
    }

    public class ViewHolder {
        public final View mView;
        public final RelativeLayout rl;
        public final LinearLayout imageName;
        public final ImageView image;
        public final ImageView imageNameIcon;
        public final TextView msg;
        public final TextView imageNameText;
        public final TextView date;
        public final TextView name;

        public ViewHolder(View view) {
            mView = view;
            rl = view.findViewById(R.id.rl);
            image = view.findViewById(R.id.image);
            msg = view.findViewById(R.id.msg);
            date = view.findViewById(R.id.date);
            imageName = view.findViewById(R.id.imagename);
            imageNameIcon = view.findViewById(R.id.imagenameicon);
            imageNameText = view.findViewById(R.id.imagenametext);
            name = view.findViewById(R.id.name);

            rl.setLayoutParams(new RelativeLayout.LayoutParams(
                    Method.get70Percent(context),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

    }
}