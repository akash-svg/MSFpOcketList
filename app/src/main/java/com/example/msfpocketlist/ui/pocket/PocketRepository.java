package com.example.msfpocketlist.ui.pocket;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.msfpocketlist.data.Pocket;
import com.example.msfpocketlist.localdb.AppDatabase;
import com.example.msfpocketlist.localdb.dao.PocketDao;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PocketRepository {
    PocketDao pocketDao;
    List<Pocket> allPocket;
    int id;
    public PocketRepository(Application application,int id){
        AppDatabase db = AppDatabase.getInstance(application);
        pocketDao = db.pocketDao();
        this.id = id;
        allPocket = pocketDao.getAllPocket(id);
    }

    public void insert(List<Pocket> pocketList){
        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                pocketDao.insertPocket(pocketList);
            }
        });
    }

    public List<Pocket> getAllPocket(){return allPocket;}

}
