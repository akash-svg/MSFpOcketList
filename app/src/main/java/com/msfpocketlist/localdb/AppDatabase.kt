package com.msfpocketlist.localdb

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msfpocketlist.data.EmployeeAll
import com.msfpocketlist.data.EmployeeEm
import com.msfpocketlist.data.EmployeeHq
import com.msfpocketlist.data.Mission
import com.msfpocketlist.data.Project

/** Offline cache only, so schema changes just drop and refill it. */
@Database(
    entities = [EmployeeHq::class, EmployeeEm::class, Mission::class, Project::class, EmployeeAll::class],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): CacheDao
}

fun createDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "pocket_list")
        .fallbackToDestructiveMigration()
        .build()

@Dao
interface CacheDao {
    @Query("SELECT * FROM mission_table")
    suspend fun missions(): List<Mission>

    @Query("SELECT * FROM pocket_table WHERE mission_id = :missionId")
    suspend fun projects(missionId: Int): List<Project>

    @Query("SELECT * FROM headquarter_table")
    suspend fun hqEmployees(): List<EmployeeHq>

    @Query("SELECT * FROM emergency_table")
    suspend fun emergencyEmployees(): List<EmployeeEm>

    @Query("SELECT * FROM all_employee WHERE project_id = :projectId")
    suspend fun employeesOfProject(projectId: Int): List<EmployeeAll>

    @Query("SELECT * FROM all_employee WHERE id = :id")
    suspend fun employee(id: Int): EmployeeAll?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(items: List<Mission>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(items: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHq(items: List<EmployeeHq>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergency(items: List<EmployeeEm>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(items: List<EmployeeAll>)
}
