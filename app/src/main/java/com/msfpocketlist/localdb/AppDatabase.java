package com.msfpocketlist.localdb;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.msfpocketlist.data.Employee;
import com.msfpocketlist.data.EmployeeAll;
import com.msfpocketlist.data.EmployeeEm;
import com.msfpocketlist.data.EmployeeHq;
import com.msfpocketlist.data.Mission;
import com.msfpocketlist.data.Project;
import com.msfpocketlist.localdb.dao.AllEmployeeDao;
import com.msfpocketlist.localdb.dao.EmployeeEmDao;
import com.msfpocketlist.localdb.dao.EmployeeHqDao;
import com.msfpocketlist.localdb.dao.MissionDao;
import com.msfpocketlist.localdb.dao.PocketDao;
import com.msfpocketlist.localdb.dao.PocketEmDao;


@Database(entities = {EmployeeHq.class, EmployeeEm.class, Mission.class, Project.class, Employee.class, EmployeeAll.class},version = 4,exportSchema = false)
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
    public abstract AllEmployeeDao allEmployeeDao();

}
