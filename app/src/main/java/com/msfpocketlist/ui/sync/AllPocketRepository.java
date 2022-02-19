package com.msfpocketlist.ui.sync;

import android.app.Application;

import com.msfpocketlist.data.Project;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.PocketDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllPocketRepository {
    PocketDao pocketDao;
    public AllPocketRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        pocketDao = db.pocketDao();
    }

    public void insert(List<Project> pocketList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                pocketDao.insertPocket(pocketList);
            }
        });
    }
}
