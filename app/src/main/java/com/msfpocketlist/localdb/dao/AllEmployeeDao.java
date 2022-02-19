package com.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.msfpocketlist.data.EmployeeAll;

import java.util.List;

@Dao
public interface AllEmployeeDao {
    @Query("SELECT * FROM all_employee WHERE id=:id")
    EmployeeAll getSingleProfile(int id);
    @Query("SELECT * FROM all_employee WHERE project_id=:id")
    List<EmployeeAll> getEmployeeList(int id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllEmployee(List<EmployeeAll> allData);
}
