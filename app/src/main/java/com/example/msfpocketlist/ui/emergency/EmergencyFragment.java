package com.example.msfpocketlist.ui.emergency;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msfpocketlist.R;


public class EmergencyFragment extends Fragment {
    View view;
    EmergencyAdapter emergencyAdapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public EmergencyFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emergency, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Emergency");

        recyclerView = view.findViewById(R.id.emergencyRec);
        emergencyAdapter = new EmergencyAdapter();
        recyclerView.setAdapter(emergencyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        SearchView searchView = (SearchView) menu.getItem(0).getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter method goes
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}