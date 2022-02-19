package com.msfpocketlist.ui.profile;

import android.app.Application;

import com.msfpocketlist.data.EmployeeAll;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.AllEmployeeDao;


public class ProfileRepository {
    AllEmployeeDao allEmployeeDao;
    EmployeeAll profile;
    public ProfileRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        allEmployeeDao = db.allEmployeeDao();
    }


    public EmployeeAll getAllPocket(int id){
        profile = allEmployeeDao.getSingleProfile(id);
        return profile;
    }
}
