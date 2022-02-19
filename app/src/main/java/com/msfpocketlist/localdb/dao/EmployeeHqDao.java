package com.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.msfpocketlist.data.EmployeeHq;

import java.util.List;


@Dao
public interface EmployeeHqDao {
    @Query("SELECT * FROM headquarter_table")
    List<EmployeeHq> getHqEmployee();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHeadquarterEmployee(List<EmployeeHq> missions);
}
