package com.msfpocketlist.ui.dashboard.hq;

import android.app.Application;
import com.msfpocketlist.data.EmployeeHq;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.EmployeeHqDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeadQuarterRepository {
    EmployeeHqDao employeeHqDao;
    List<EmployeeHq> employeeHqList;

    public HeadQuarterRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        employeeHqDao = db.employeeHqDao();
        employeeHqList = employeeHqDao.getHqEmployee();
    }

    public void insert(List<EmployeeHq> employeeHqList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                employeeHqDao.insertHeadquarterEmployee(employeeHqList);
            }
        });

    }

    public List<EmployeeHq> getAllHq() {
        return employeeHqList;
    }
}
