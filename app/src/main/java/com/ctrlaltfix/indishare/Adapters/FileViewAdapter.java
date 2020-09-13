package com.ctrlaltfix.indishare.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ctrlaltfix.indishare.Models.FileModel;
import com.ctrlaltfix.indishare.Models.SendFileDetailsModel;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.ViewHolder> {
    private List<String> item;
    private List<String> path;
    Context context;
    Boolean isRoot;
    String root= Environment.getExternalStorageDirectory().getPath();

    public FileViewAdapter(List<String> item, List<String> path, Context context, Boolean isRoot) {
        this.item = item;
        this.path = path;
        this.context = context;
        this.isRoot = isRoot;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(!isRoot && position == 0)
        {
            holder.check.setVisibility(View.INVISIBLE);
        }

        if (isDirectory(position)){
            holder.check.setVisibility(View.GONE);
        }else{
            holder.check.setVisibility(View.VISIBLE);
        }


        holder.fileName.setText(item.get(position));
        holder.icon.setImageResource(setFileImageType(new File(path.get(position))));
        holder.date.setText(getLastDate(position));
        holder.totalFiles.setText(getSize(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File isFile=new File(path.get(position));
                if(isFile.isDirectory())
                {
                    Method.getDirFromRoot(isFile.toString(), context, root);
                }
                else
                {
                    String type = "Other";
                    if (getName(position).contains(".3g2") || getName(position).contains(".3gp") || getName(position).contains(".avi") || getName(position).contains(".flv") || getName(position).contains(".h264") || getName(position).contains(".m4v") || getName(position).contains(".mkv") || getName(position).contains(".mov") || getName(position).contains(".mp4") || getName(position).contains(".mpeg") || getName(position).contains(".mpg") || getName(position).contains(".rm") || getName(position).contains(".swf") || getName(position).contains(".vob") || getName(position).contains(".wmv")){
                        type = "Videos";
                    }else if(getName(position).contains(".ai") || getName(position).contains(".bmp") || getName(position).contains(".gif") || getName(position).contains(".ico") || getName(position).contains(".jpeg") || getName(position).contains(".jpg") || getName(position).contains(".png") || getName(position).contains(".ps") || getName(position).contains(".psd") || getName(position).contains(".svg") || getName(position).contains(".tif") || getName(position).contains(".tiff")){
                        type = "Images";
                    }else if(getName(position).contains(".aif") || getName(position).contains(".cda") || getName(position).contains(".mid") || getName(position).contains(".midi") || getName(position).contains(".mp3") || getName(position).contains(".mpa") || getName(position).contains(".ogg") || getName(position).contains(".wav") || getName(position).contains(".wma") || getName(position).contains(".wpl")){
                        type = "Audio";
                    }else if(getName(position).contains(".apk")){
                        type = "APK";
                    }else if(getName(position).contains(".doc") || getName(position).contains(".docx") || getName(position).contains(".odt") || getName(position).contains(".pdf") || getName(position).contains(".rtf") || getName(position).contains(".tex") || getName(position).contains(".txt") || getName(position).contains(".wpd") || getName(position).contains(".key") || getName(position).contains(".odp") || getName(position).contains(".pps") || getName(position).contains(".ppt") || getName(position).contains(".pptx") || getName(position).contains(".ods") || getName(position).contains(".xls") || getName(position).contains(".xlsm") || getName(position).contains(".xlsx")){
                        type = "Documents";
                    }else if(getName(position).contains(".7z") || getName(position).contains(".arj") || getName(position).contains(".deb") || getName(position).contains(".pkg") || getName(position).contains(".rar") || getName(position).contains(".rpm") || getName(position).contains(".tar") || getName(position).contains(".gz") || getName(position).contains(".z") || getName(position).contains(".zip")){
                        type = "Compressed";
                    }
                    final SendFileDetailsModel model = new SendFileDetailsModel(getName(position), getSize(position), type, getSizeInBytes(position));
                    Uri uri = getUri(position);
                    if (!Constant.sendUri.contains(uri)){
                        add(uri, model, holder);
                    }else{
                        remove(uri, model, holder);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView check;
        ImageView icon;
        TextView fileName;
        TextView date;
        TextView totalFiles;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            check =  view.findViewById(R.id.check);
            icon =  view.findViewById(R.id.icon);
            fileName =  view.findViewById(R.id.fileName);
            date =  view.findViewById(R.id.date);
            totalFiles =  view.findViewById(R.id.totalFiles);
        }

    }

    private void add(Uri uri, final SendFileDetailsModel model, final FileViewAdapter.ViewHolder holder){
        Constant.sendUri.add(uri);
        Constant.sendOriginalUri.add(uri);
        Constant.sendUriDetails.add(model);
        holder.check.setImageDrawable(context.getResources().getDrawable(R.drawable.checked_image));
    }

    private void remove(Uri uri, final SendFileDetailsModel model, final FileViewAdapter.ViewHolder holder){
        Constant.sendUri.remove(uri);
        Constant.sendOriginalUri.remove(uri);
        Constant.sendUriDetails.remove(model);
        holder.check.setImageDrawable(context.getResources().getDrawable(R.drawable.unchecked_image));
    }

    private int setFileImageType(File file)
    {
        int lastIndex=file.getAbsolutePath().lastIndexOf(".");
        String filepath=file.getAbsolutePath();
        if (file.isDirectory())
            return R.drawable.ic_baseline_folder_24;
        else
        {
            return R.drawable.ic_baseline_insert_drive_file_24;
        }
    }

    String getLastDate(int pos)
    {
        File file=new File(path.get(pos));
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(file.lastModified());
    }

    String getSize(int pos){
        File file=new File(path.get(pos));
        if (file.isDirectory()){
            return String.valueOf(file.listFiles().length);
        }else {
            long size = getSizeInBytes(pos);
            if (size > (1024 * 1024 * 1024)) {
                return String.format("%.2f", (double) size / (1024 * 1024 * 1024)) + " GB";
            }else if (size > (1024 * 1024)) {
                return String.format("%.2f", (double) size / (1024 * 1024)) + " MB";
            }else if (size > (1024)) {
                return String.format("%.2f", (double) size / (1024)) + " KB";
            }else {
                return String.format("%.2f", (double) size) + " B";
            }
        }
    }
    String getName(int pos){
        File file=new File(path.get(pos));
        return file.getName();
    }
    Uri getUri(int pos){
        File file=new File(path.get(pos));
        return Uri.fromFile(file);
    }

    long getSizeInBytes(int pos){
        File file=new File(path.get(pos));
        return file.length();
    }
    boolean isDirectory(int pos){
        File file=new File(path.get(pos));
        return file.isDirectory();
    }
}