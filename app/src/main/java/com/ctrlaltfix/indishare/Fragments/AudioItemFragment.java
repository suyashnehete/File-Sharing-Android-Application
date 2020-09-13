package com.ctrlaltfix.indishare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlaltfix.indishare.Adapters.AudioViewAdapter;
import com.ctrlaltfix.indishare.Adapters.VideoViewAdapter;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.SendReceiveActivity;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.ctrlaltfix.indishare.Utils.Method;

public class AudioItemFragment extends Fragment {

    public static RecyclerView.Adapter adapter;
    public static RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AudioItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AudioItemFragment newInstance() {
        AudioItemFragment fragment = new AudioItemFragment();
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
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new AudioViewAdapter(Constant.allAudioListModificationTime, getContext());
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}