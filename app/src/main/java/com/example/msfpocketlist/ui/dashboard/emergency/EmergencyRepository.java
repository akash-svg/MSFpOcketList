package com.example.msfpocketlist.ui.dashboard.emergency;

import android.app.Application;
import com.example.msfpocketlist.data.EmployeeEm;
import com.example.msfpocketlist.localdb.AppDatabase;
import com.example.msfpocketlist.localdb.dao.EmployeeEmDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmergencyRepository {
    EmployeeEmDao employeeEmDao;
    List<EmployeeEm> employeeEmList;

    public EmergencyRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        employeeEmDao = db.employeeEmDao();
        employeeEmList = employeeEmDao.getEmEmployee();
    }

    public void insert(List<EmployeeEm> employeeEmList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                employeeEmDao.insertEmergencyEmployee(employeeEmList);
            }
        });

    }

    public List<EmployeeEm> getAllEm() {
        return employeeEmList;
    }
}
