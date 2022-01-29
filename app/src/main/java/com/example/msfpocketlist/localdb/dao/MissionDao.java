package com.example.msfpocketlist.localdb.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.msfpocketlist.data.Mission;

import java.util.List;


@Dao
public interface MissionDao {
    @Query("SELECT * FROM mission_table")
    List<Mission> getAllMission();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMission(List<Mission> missions);
}
