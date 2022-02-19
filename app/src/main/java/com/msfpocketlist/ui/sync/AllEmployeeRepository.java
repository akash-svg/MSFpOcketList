package com.msfpocketlist.ui.sync;

import android.app.Application;

import com.msfpocketlist.data.EmployeeAll;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.AllEmployeeDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllEmployeeRepository {
    AllEmployeeDao allEmployeeDao;
    public AllEmployeeRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        allEmployeeDao = db.allEmployeeDao();
    }

    public void insert(List<EmployeeAll> allData){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                allEmployeeDao.insertAllEmployee(allData);
            }
        });
    }

}
