package com.msfpocketlist.ui.sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.EmergencyModel;
import com.msfpocketlist.data.HqModel;
import com.msfpocketlist.data.MissionModel;
import com.msfpocketlist.data.PocketModel;
import com.msfpocketlist.data.UserInfoOne;
import com.msfpocketlist.databinding.ActivitySyncBinding;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.dashboard.emergency.EmergencyRepository;
import com.msfpocketlist.ui.dashboard.hq.HeadQuarterRepository;
import com.msfpocketlist.ui.dashboard.mission.MissionRepository;
import com.msfpocketlist.util.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener {
    ActivitySyncBinding binding;
    ApiInterface apiInterface;
    HeadQuarterRepository headQuarterRepository;
    EmergencyRepository emergencyRepository;
    MissionRepository missionRepository;
    AllPocketRepository allPocketRepository;
    AllEmployeeRepository allEmployeeRepository;
    NetworkReceiver receiver;
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
        allEmployeeRepository = new AllEmployeeRepository(getApplication());

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);

        binding.emergencySyncBtn.setOnClickListener(v->{
            if (NetworkReceiver.isConnected()){
                getEmployeeEmList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.hqSyncBtn.setOnClickListener(v->{
            if (NetworkReceiver.isConnected()){
                getEmployeeHqList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.missionSyncBtn.setOnClickListener(v->{
            if (NetworkReceiver.isConnected()){
                    getMissionList();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.pocketSyncBtn.setOnClickListener(v->{
            if (NetworkReceiver.isConnected()){
                getAllPockets();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.allEmployeeData.setOnClickListener(v->{
            if (NetworkReceiver.isConnected()){
                getAllEmployeeData();
            }else{
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllEmployeeData() {
        DataManager.getInstance().showProgressMessage(this);
        Call<UserInfoOne> call = apiInterface.getAllEmployee(Constant.APIKEY);
        call.enqueue(new Callback<UserInfoOne>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoOne> call, @NonNull Response<UserInfoOne> response) {
                try {
                    UserInfoOne allData = response.body();

                    if (allData != null) {
                        if (allData.response == 200) {
                            allEmployeeRepository.insert(allData.employees);
                            Toast.makeText(SyncActivity.this, "All Employee data sync completed", Toast.LENGTH_SHORT).show();
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
            public void onFailure(@NonNull Call<UserInfoOne> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                            allPocketRepository.insert(pocketModel.projects);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
            binding.conLay.getRoot().setVisibility(View.GONE);
        }else{
            binding.conLay.getRoot().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,BaseClass.intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}