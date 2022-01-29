package com.example.msfpocketlist.localdb;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.msfpocketlist.data.EmployeeEm;
import com.example.msfpocketlist.data.EmployeeHq;
import com.example.msfpocketlist.data.Mission;
import com.example.msfpocketlist.data.Pocket;
import com.example.msfpocketlist.data.PocketEm;
import com.example.msfpocketlist.localdb.dao.EmployeeEmDao;
import com.example.msfpocketlist.localdb.dao.EmployeeHqDao;
import com.example.msfpocketlist.localdb.dao.MissionDao;
import com.example.msfpocketlist.localdb.dao.PocketDao;
import com.example.msfpocketlist.localdb.dao.PocketEmDao;

@Database(entities = {EmployeeHq.class, EmployeeEm.class, Mission.class, Pocket.class, PocketEm.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase INSTANCE;
    public static synchronized AppDatabase getInstance(Context context){
        if (INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"pocket_list")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return INSTANCE;
    }

    public abstract EmployeeHqDao employeeHqDao();
    public abstract EmployeeEmDao employeeEmDao();
    public abstract MissionDao missionDao();
    public abstract PocketDao pocketDao();
    public abstract PocketEmDao pocketEmDao();

}
