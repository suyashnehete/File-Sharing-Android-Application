package com.ctrlaltfix.indishare.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.ctrlaltfix.indishare.Adapters.TrackDataAdapter;
import com.ctrlaltfix.indishare.Utils.Constant;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ctrlaltfix.indishare.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     TrackBottomSheetFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class TrackBottomSheetFragment extends BottomSheetDialogFragment {

    public static RecyclerView.Adapter adapter;
    // TODO: Customize parameter argument names

    // TODO: Customize parameters
    public static TrackBottomSheetFragment newInstance() {
        final TrackBottomSheetFragment fragment = new TrackBottomSheetFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public void close(){
        this.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_bottom_sheet_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView =  view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(20);
        adapter = new TrackDataAdapter(Constant.trackDataModels, getContext());
        final ImageButton btn = view.findViewById(R.id.minimize);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        recyclerView.setAdapter(adapter);
    }

}