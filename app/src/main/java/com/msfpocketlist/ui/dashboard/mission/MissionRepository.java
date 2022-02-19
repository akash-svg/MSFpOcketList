package com.msfpocketlist.ui.dashboard.mission;

import android.app.Application;
import com.msfpocketlist.data.Mission;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.MissionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MissionRepository {
    MissionDao missionDao;
    List<Mission> allMission;
    
    public MissionRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        missionDao = db.missionDao();
        allMission = missionDao.getAllMission();
    }

    public void insert(List<Mission> missionList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.insertMission(missionList);
            }
        });

    }

    public List<Mission> getAllMission() {
        return allMission;
    }


}
