package com.example.msfpocketlist.localdb.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.msfpocketlist.data.Pocket;

import java.util.List;


@Dao
public interface PocketDao {
    @Query("SELECT * FROM pocket_table WHERE mission=:missionId")
    List<Pocket> getAllPocket(int missionId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPocket(List<Pocket> pockets);
}
