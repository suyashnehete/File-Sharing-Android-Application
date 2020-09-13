package com.ctrlaltfix.indishare.ChatSection.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctrlaltfix.indishare.ChatSection.Models.ContactList;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Method;

import java.util.ArrayList;

import static com.ctrlaltfix.indishare.ChatSection.AddMemberActivity.mainArray;

public class AddMembersAdapter extends BaseAdapter {
    ArrayList<ContactList> contactList;
    Context context;

    public AddMembersAdapter(ArrayList<ContactList> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_add_members, parent, false));
        final ContactList model = contactList.get(position);

        Glide.with(context).load(model.getProfile()).into(holder.profile);
        holder.name.setText(model.getName());
        holder.phone.setText(model.getMobile());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.selected.getVisibility() == View.VISIBLE){
                    mainArray.remove(model.getId());
                    holder.selected.setVisibility(View.GONE);
                }else{
                    mainArray.add(model.getId());
                    holder.selected.setVisibility(View.VISIBLE);
                }
            }
        });

        return holder.mView;
    }

    public class ViewHolder {
        public final View mView;
        public final View selected;
        public final ImageView profile;
        public final TextView name;
        public final TextView phone;

        public ViewHolder(View view) {
            mView = view;
            selected = view.findViewById(R.id.selected);
            profile = view.findViewById(R.id.profile);
            phone = view.findViewById(R.id.phone);
            name = view.findViewById(R.id.name);
        }

    }
}
