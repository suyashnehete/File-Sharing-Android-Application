package com.ctrlaltfix.indishare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.Utils.Method;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileItemFragment extends Fragment {

    public static RecyclerView recyclerView;
    public static ArrayList<File>  fileList;
    public static ArrayList<String>  fileListName;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FileItemFragment newInstance() {
        FileItemFragment fragment = new FileItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        final Context context = view.getContext();
        Spinner storage = view.findViewById(R.id.storage);
        File file = new File("/storage");
        fileList = new ArrayList<>();
        fileListName = new ArrayList<>();
        if (file.exists()) {
            for (File f : file.listFiles()) {
                Log.d("suyash", f.toString());
                if ((f.isDirectory() && f.canRead() && f.exists() && !fileList.contains(f)) || f.toString().contains("emulated")) {
                    if (f.toString().contains("emulated")) {
                        fileList.add(new File(f + "/0"));
                        fileListName.add("Internal Storage");
                    } else {
                        fileList.add(f);
                        fileListName.add("SD Card");
                    }
                }
            }
        }

        Collections.reverse(fileList);
        Collections.reverse(fileListName);

        storage.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, fileListName));
        storage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String root = fileList.get(i).toString();

                Method.getDirFromRoot(root, context, root);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }
}
