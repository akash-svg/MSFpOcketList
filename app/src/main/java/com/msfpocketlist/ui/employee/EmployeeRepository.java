package com.msfpocketlist.ui.employee;

import android.app.Application;

import com.msfpocketlist.data.Employee;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.PocketEmDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmployeeRepository {
    PocketEmDao pocketEmDao;
    List<Employee> pocketEms;
    int missionId;
    int pocketId;

    public EmployeeRepository(Application application, int missionId, int pocketId) {
        AppDatabase db = AppDatabase.getInstance(application);
        pocketEmDao = db.pocketEmDao();
        this.missionId = missionId;
        this.pocketId = pocketId;
        pocketEms = pocketEmDao.getAllPocketEm(pocketId);
    }

    public void insert(List<Employee> pocketEmList) {
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                pocketEmDao.insertPocketEm(pocketEmList);
            }
        });
    }

    public List<Employee> getAllPocketEm() {
        return pocketEms;
    }
}
