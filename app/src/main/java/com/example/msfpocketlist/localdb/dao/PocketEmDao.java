package com.example.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.msfpocketlist.data.PocketEm;
import java.util.List;

@Dao
public interface PocketEmDao {
    @Query("SELECT * FROM pocket_on_employee WHERE mission=:missionId AND pocket=:pocketId")
    List<PocketEm> getAllPocketEm(int missionId, int pocketId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPocketEm(List<PocketEm> pocketEms);
}
