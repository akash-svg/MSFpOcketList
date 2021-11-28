package com.example.msfpocketlist.ui.mission;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msfpocketlist.R;


public class MissionFragment extends Fragment {
    View view;
    MissionAdapter missionAdapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MissionFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mission, container, false);

        recyclerView = view.findViewById(R.id.missionRec);

        missionAdapter = new MissionAdapter();
        recyclerView.setAdapter(missionAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Mission");
        return view;
    }
}