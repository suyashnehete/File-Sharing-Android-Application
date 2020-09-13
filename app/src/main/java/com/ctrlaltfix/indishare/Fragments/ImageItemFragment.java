package com.ctrlaltfix.indishare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlaltfix.indishare.Adapters.ImageViewAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.SendReceiveActivity;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;

public class ImageItemFragment extends Fragment {

    public static RecyclerView.Adapter adapter;

    public static int width = 0;
    public static RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ImageItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ImageItemFragment newInstance() {
        ImageItemFragment fragment = new ImageItemFragment();
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
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view;
            String result = Method.calculateNoOfColumns(getContext(), 100);
            int grid = Integer.parseInt(result.split("<>")[0]);
            width = Integer.parseInt(result.split("<>")[1]);
            recyclerView.setLayoutManager(new GridLayoutManager(context, grid));
            adapter = new ImageViewAdapter(Constant.allImageListModificationTime, getContext(), width);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}