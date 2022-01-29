package com.example.msfpocketlist.ui.employee;

import android.app.Application;
import com.example.msfpocketlist.data.PocketEm;
import com.example.msfpocketlist.localdb.AppDatabase;
import com.example.msfpocketlist.localdb.dao.PocketEmDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmployeeRepository {
    PocketEmDao pocketEmDao;
    List<PocketEm> pocketEms;
    int missionId;
    int pocketId;

    public EmployeeRepository(Application application, int missionId, int pocketId) {
        AppDatabase db = AppDatabase.getInstance(application);
        pocketEmDao = db.pocketEmDao();
        this.missionId = missionId;
        this.pocketId = pocketId;
        pocketEms = pocketEmDao.getAllPocketEm(missionId, pocketId);
    }

    public void insert(List<PocketEm> pocketEmList) {
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                pocketEmDao.insertPocketEm(pocketEmList);
            }
        });
    }

    public List<PocketEm> getAllPocketEm() {
        return pocketEms;
    }
}
