package com.msfpocketlist.syncutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.EmergencyModel;
import com.msfpocketlist.data.HqModel;
import com.msfpocketlist.data.MissionModel;
import com.msfpocketlist.data.PocketModel;
import com.msfpocketlist.data.UserInfoOne;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.dashboard.emergency.EmergencyRepository;
import com.msfpocketlist.ui.dashboard.hq.HeadQuarterRepository;
import com.msfpocketlist.ui.dashboard.mission.MissionRepository;
import com.msfpocketlist.ui.sync.AllEmployeeRepository;
import com.msfpocketlist.ui.sync.AllPocketRepository;
import com.msfpocketlist.util.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {
    public boolean isThreadRunning = true;
    Handler handler = null;
    Runnable runnable;
    ApiInterface apiInterface;
    AppDatabase db;
    HeadQuarterRepository headQuarterRepository;
    EmergencyRepository emergencyRepository;
    MissionRepository missionRepository;
    AllPocketRepository allPocketRepository;
    AllEmployeeRepository allEmployeeRepository;
    String channel_id;
    String group_id;
    NotificationManagerCompat notificationManager;
    int counter = 0;

    public SyncService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //initialization
        headQuarterRepository = new HeadQuarterRepository(getApplication());
        emergencyRepository = new EmergencyRepository(getApplication());
        missionRepository = new MissionRepository(getApplication());
        allPocketRepository = new AllPocketRepository(getApplication());
        allEmployeeRepository = new AllEmployeeRepository(getApplication());
        //local
        db = AppDatabase.getInstance(getApplicationContext());
        //remote
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //notification manager
        notificationManager = NotificationManagerCompat.from(SyncService.this);
        // create notification channel - if not already present
        CreateNotificationChannel(notificationManager, (channel_id = group_id = getPackageName() + "." + getClass().getSimpleName()));
        //notification
        startMyOwnForeground("Syncing",0,true);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        isThreadRunning = true;
        startHandler();
        return START_NOT_STICKY;
    }

    public void startHandler() {
        if (isThreadRunning) {
            handler.postDelayed(getRunnable(), 0);
        }
    }

    @NonNull
    private Runnable getRunnable() {
        return runnable = new Runnable() {
            @Override
            public void run() {
                if (isThreadRunning) {
                    if (counter==0){
                        getAllEmployeeData();
                    }else if (counter==1){
                        getAllPockets();
                    }else if (counter==2){
                        getEmployeeEmList();
                    }else if (counter==3){
                        getEmployeeHqList();
                    }else if (counter==4){
                        getMissionList();
                    }else {
                        SyncUtil.deleteUser(getApplicationContext());
                        SyncUtil.writeString(getApplicationContext(),Constant.SYNC_INFO, DataManager.getInstance().currentDate());
                        stopSelf();
                    }
                }
                handler.postDelayed(this, 10000);
            }

        };
    }

    //1.get all employee data
    private void getAllEmployeeData() {
        Call<UserInfoOne> call = apiInterface.getAllEmployee(Constant.APIKEY);
        call.enqueue(new Callback<UserInfoOne>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoOne> call, @NonNull Response<UserInfoOne> response) {
                try {
                    UserInfoOne allData = response.body();
                    if (allData != null) {
                        if (allData.response == 200) {
                            allEmployeeRepository.insert(allData.employees);
                        }
                    }
                    startMyOwnForeground("20%",20,false);
                    counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoOne> call, @NonNull Throwable t) { }
        });
    }


    //2.get all pocket data
    private void getAllPockets() {
        Call<PocketModel> call = apiInterface.getActivePockets(Constant.APIKEY);
        call.enqueue(new Callback<PocketModel>() {
            @Override
            public void onResponse(@NonNull Call<PocketModel> call, @NonNull Response<PocketModel> response) {
                try {
                    PocketModel pocketModel = response.body();
                    if (pocketModel != null) {
                        if (pocketModel.response == 200) {
                            allPocketRepository.insert(pocketModel.projects);
                        }
                    }
                    startMyOwnForeground("40%",40,false);
                    counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<PocketModel> call, @NonNull Throwable t) { }
        });
    }

    //4.get all hq employee data
    private void getEmployeeHqList() {
        Call<HqModel> call = apiInterface.getHeadquarterList(Constant.APIKEY);
        call.enqueue(new Callback<HqModel>() {
            @Override
            public void onResponse(@NonNull Call<HqModel> call, @NonNull Response<HqModel> response) {
                try {
                    HqModel hqModel = response.body();
                    if (hqModel != null) {
                        if (hqModel.response == 200) {
                            headQuarterRepository.insert(hqModel.employees);
                        }
                    }
                    startMyOwnForeground("80%",80,false);
                    counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HqModel> call, @NonNull Throwable t) { }
        });
    }

    //3.get all emergency employee data
    private void getEmployeeEmList() {
        Call<EmergencyModel> call = apiInterface.getEmergencyList(Constant.APIKEY);
        call.enqueue(new Callback<EmergencyModel>() {
            @Override
            public void onResponse(@NonNull Call<EmergencyModel> call, @NonNull Response<EmergencyModel> response) {
                try {
                    EmergencyModel emergencyModel = response.body();
                    if (emergencyModel != null) {
                        if (emergencyModel.response == 200) {
                            emergencyRepository.insert(emergencyModel.employees);
                        }
                    }
                    startMyOwnForeground("60%",60,false);
                    counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmergencyModel> call, @NonNull Throwable t) { }
        });
    }

    //5.get all mission data
    private void getMissionList() {
        Call<MissionModel> call = apiInterface.getAllMission(Constant.APIKEY);
        call.enqueue(new Callback<MissionModel>() {
            @Override
            public void onResponse(@NonNull Call<MissionModel> call, @NonNull Response<MissionModel> response) {
                try {
                    MissionModel missionModel = response.body();
                    if (missionModel != null) {
                        if (missionModel.response == 200) {
                            missionRepository.insert(missionModel.missions);
                        }
                    }
                    startMyOwnForeground("100%",100,false);
                    counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MissionModel> call, @NonNull Throwable t) { }
        });
    }


    //notification channel related related
    private void CreateNotificationChannel(NotificationManagerCompat notificationManager, String channel_id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channel_id,
                    getString(R.string.data_sync_service_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(getString(R.string.complete_data_backup_service_notification_channel_desc));
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startMyOwnForeground(String title,int progress,boolean runTime) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("MSF KM PocketList")
                .setContentText(title)
                .setProgress(100,progress,false)
                .setPriority(NotificationManagerCompat.IMPORTANCE_LOW)
                .build();


        if (runTime){
            startForeground(2, notification);
        }else{
            notificationManager.notify(2,notification);
        }
    }
}
