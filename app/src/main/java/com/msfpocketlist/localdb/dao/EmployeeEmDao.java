package com.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.msfpocketlist.data.EmployeeEm;
import java.util.List;

@Dao
public interface EmployeeEmDao {
    @Query("SELECT * FROM emergency_table")
    List<EmployeeEm> getEmEmployee();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmergencyEmployee(List<EmployeeEm> missions);
}
