package com.example.msfpocketlist.ui.dashboard.mission;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.Mission;
import com.example.msfpocketlist.data.MissionModel;
import com.example.msfpocketlist.databinding.FragmentMissionBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.pocket.PocketActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.example.msfpocketlist.util.network.ConnectionLiveData;
import com.example.msfpocketlist.util.network.ConnectionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MissionFragment extends Fragment implements MissionAdapter.OnItemClick {
    private FragmentMissionBinding binding;
    List<Mission> mission = new ArrayList<>();
    MissionAdapter missionAdapter;
    ApiInterface apiInterface;
    MissionAdapter.OnItemClick onItemClick;
    MissionRepository repository;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MissionFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMissionBinding.inflate(inflater, container, false);
        repository = new MissionRepository(getActivity().getApplication());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Mission");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        onItemClick = this;
        binding.missionRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.missionRec.setHasFixedSize(true);
        missionAdapter = new MissionAdapter(mission, getContext(), onItemClick);
        binding.missionRec.setAdapter(missionAdapter);
        if (ConnectionManager.connection(requireActivity())) {
            getMissionList();
        } else {
            getCacheData();
        }

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
        mission.clear();
        try {
            List<Mission> a = repository.getAllMission();
            if (a.size() != 0) {
                binding.noDataFound.setVisibility(View.GONE);
                mission.addAll(a);
                missionAdapter.notifyDataSetChanged();
            } else {
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(requireActivity().getString(R.string.no_data_found));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMissionList() {
        DataManager.getInstance().showProgressMessage(getActivity());
        Call<MissionModel> call = apiInterface.getAllMission(Constant.APIKEY);
        call.enqueue(new Callback<MissionModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MissionModel> call, @NonNull Response<MissionModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    MissionModel missionModel = response.body();
                    if (missionModel != null) {
                        mission.clear();
                        if (missionModel.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            mission.addAll(missionModel.missions);
                            missionAdapter.notifyDataSetChanged();
                        } else {
                            getCacheData();
                        }
                    } else {
                        getCacheData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MissionModel> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                List<Mission> filteredList = new ArrayList<>();
                for (Mission data : mission) {
                    if (data.title.toLowerCase().contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    missionAdapter.searchList(filteredList);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public void onMissionItemClick(Mission mission) {
       startActivity(new Intent(requireActivity(), PocketActivity.class).putExtra("missionId",mission.id));
    }


}