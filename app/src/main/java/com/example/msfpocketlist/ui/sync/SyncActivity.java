package com.example.msfpocketlist.ui.sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.EmergencyModel;
import com.example.msfpocketlist.data.HqModel;
import com.example.msfpocketlist.data.MissionModel;
import com.example.msfpocketlist.data.PocketModel;
import com.example.msfpocketlist.databinding.ActivitySyncBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.dashboard.emergency.EmergencyRepository;
import com.example.msfpocketlist.ui.dashboard.hq.HeadQuarterRepository;
import com.example.msfpocketlist.ui.dashboard.mission.MissionRepository;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncActivity extends AppCompatActivity {
    ActivitySyncBinding binding;
    ApiInterface apiInterface;
    HeadQuarterRepository headQuarterRepository;
    EmergencyRepository emergencyRepository;
    MissionRepository missionRepository;
    AllPocketRepository allPocketRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() !=null){
            getSupportActionBar().setTitle("Sync");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        headQuarterRepository = new HeadQuarterRepository(getApplication());
        emergencyRepository = new EmergencyRepository(getApplication());
        missionRepository = new MissionRepository(getApplication());
        allPocketRepository = new AllPocketRepository(getApplication());
        binding.emergencySyncBtn.setOnClickListener(v->{
            if (ConnectionManager.connection(this)){
                getEmployeeEmList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.hqSyncBtn.setOnClickListener(v->{
            if (ConnectionManager.connection(this)){
                getEmployeeHqList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.missionSyncBtn.setOnClickListener(v->{
            if (ConnectionManager.connection(this)){
                    getMissionList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.pocketSyncBtn.setOnClickListener(v->{
            if (ConnectionManager.connection(this)){
                getAllPockets();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllPockets() {
        DataManager.getInstance().showProgressMessage(this);
        Call<PocketModel> call = apiInterface.getActivePockets(Constant.APIKEY);
        call.enqueue(new Callback<PocketModel>() {
            @Override
            public void onResponse(@NonNull Call<PocketModel> call, @NonNull Response<PocketModel> response) {
                try {
                    PocketModel pocketModel = response.body();
                    if (pocketModel != null) {
                        if (pocketModel.response == 200) {
                            allPocketRepository.insert(pocketModel.pockets);
                            Toast.makeText(SyncActivity.this, "Pocket data sync completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SyncActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SyncActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DataManager.getInstance().hideProgressMessage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PocketModel> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEmployeeHqList() {
        DataManager.getInstance().showProgressMessage(this);
        Call<HqModel> call = apiInterface.getHeadquarterList(Constant.APIKEY);
        call.enqueue(new Callback<HqModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<HqModel> call, @NonNull Response<HqModel> response) {
                try {
                    HqModel hqModel = response.body();
                    if (hqModel != null) {
                        if (hqModel.response == 200) {
                            headQuarterRepository.insert(hqModel.employees);
                            Toast.makeText(SyncActivity.this, "Headquarter data sync completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SyncActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SyncActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DataManager.getInstance().hideProgressMessage();
                }

            }

            @Override
            public void onFailure(@NonNull Call<HqModel> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getEmployeeEmList() {
        DataManager.getInstance().showProgressMessage(this);
        Call<EmergencyModel> call = apiInterface.getEmergencyList(Constant.APIKEY);
        call.enqueue(new Callback<EmergencyModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<EmergencyModel> call, @NonNull Response<EmergencyModel> response) {
                try {
                    EmergencyModel emergencyModel = response.body();
                    if (emergencyModel != null) {
                        if (emergencyModel.response == 200) {
                            emergencyRepository.insert(emergencyModel.employees);
                            Toast.makeText(SyncActivity.this, "Emergency data sync completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SyncActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SyncActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DataManager.getInstance().hideProgressMessage();
                }

            }

            @Override
            public void onFailure(@NonNull Call<EmergencyModel> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMissionList() {
        DataManager.getInstance().showProgressMessage(this);
        Call<MissionModel> call = apiInterface.getAllMission(Constant.APIKEY);
        call.enqueue(new Callback<MissionModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MissionModel> call, @NonNull Response<MissionModel> response) {
                try {
                    MissionModel missionModel = response.body();
                    if (missionModel != null) {
                        if (missionModel.response == 200) {
                            missionRepository.insert(missionModel.missions);
                            Toast.makeText(SyncActivity.this, "Mission data sync completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SyncActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SyncActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DataManager.getInstance().hideProgressMessage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MissionModel> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}