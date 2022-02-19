package com.msfpocketlist.ui.pocket;

import android.app.Application;

import com.msfpocketlist.data.Project;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.localdb.dao.PocketDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PocketRepository {
    PocketDao pocketDao;
    List<Project> allPocket;
    int id;
    public PocketRepository(Application application,int id){
        AppDatabase db = AppDatabase.getInstance(application);
        pocketDao = db.pocketDao();
        this.id = id;
        allPocket = pocketDao.getAllPocket(id);
    }

    public void insert(List<Project> pocketList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                pocketDao.deleteAll();
                pocketDao.insertPocket(pocketList);
            }
        });
    }

    public List<Project> getAllPocket(){return allPocket;}

}
