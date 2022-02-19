package com.msfpocketlist.localdb.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.msfpocketlist.data.Project;

import java.util.List;


@Dao
public interface PocketDao {
    @Query("SELECT * FROM pocket_table WHERE mission_id=:missionId")
    List<Project> getAllPocket(int missionId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPocket(List<Project> pockets);
    @Query("DELETE FROM pocket_table")
    abstract void deleteAll();
}
