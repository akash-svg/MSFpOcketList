package com.example.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.msfpocketlist.data.EmployeeEm;
import com.example.msfpocketlist.data.EmployeeHq;

import java.util.List;

@Dao
public interface EmployeeEmDao {
    @Query("SELECT * FROM emergency_table")
    List<EmployeeEm> getEmEmployee();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmergencyEmployee(List<EmployeeEm> missions);
}
