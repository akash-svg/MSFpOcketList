package com.msfpocketlist.localdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.msfpocketlist.data.Employee;

import java.util.List;


@Dao
public interface PocketEmDao {
    @Query("SELECT * FROM pocket_on_employee WHERE  id=:pocketId")
    List<Employee> getAllPocketEm(int pocketId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPocketEm(List<Employee> pocketEms);
}
