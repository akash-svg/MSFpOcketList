package com.msfpocketlist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Everything is nullable with a default: Gson fills these reflectively and the API omits fields freely.

data class MissionModel(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("missions") val missions: List<Mission>? = null,
)

data class PocketModel(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("projects") val projects: List<Project>? = null,
)

data class HqModel(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("employees") val employees: List<EmployeeHq>? = null,
)

data class EmergencyModel(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("employees") val employees: List<EmployeeEm>? = null,
)

data class PocketEmModel(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("employees") val employees: List<Employee>? = null,
)

data class UserInfoOne(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("employees") val employees: List<EmployeeAll>? = null,
)

data class UserInfo(
    @SerializedName("message") val message: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("profile") val profile: Profile? = null,
) {
    data class Profile(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("mission_title") val missionTitle: String? = null,
        @SerializedName("department") val department: String? = null,
        @SerializedName("designation") val designation: String? = null,
        @SerializedName("full_name") val fullName: String? = null,
        @SerializedName("avatar") val avatar: String? = null,
        @SerializedName("mobile_no_1") val mobileNo1: String? = null,
        @SerializedName("mobile_no_2") val mobileNo2: String? = null,
        @SerializedName("email_id") val emailId: String? = null,
    )
}

@Entity(tableName = "mission_table")
data class Mission(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") val id: Int? = null,
    @ColumnInfo(name = "title") @SerializedName("title") val title: String? = null,
)

@Entity(tableName = "pocket_table")
data class Project(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") val id: Int? = null,
    @ColumnInfo(name = "mission_id") @SerializedName("mission_id") val missionId: Int? = null,
    @ColumnInfo(name = "title") @SerializedName("title") val title: String? = null,
)

/** Project staff as returned by the API; offline lists come from [EmployeeAll] instead. */
data class Employee(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("mission_title") val missionTitle: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("mobile_no_1") val mobileNo1: String? = null,
    @SerializedName("mobile_no_2") val mobileNo2: String? = null,
    @SerializedName("email_id") val emailId: String? = null,
    @SerializedName("department") val department: String? = null,
    @SerializedName("designation") val designation: String? = null,
)

@Entity(tableName = "all_employee")
data class EmployeeAll(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") val id: Int? = null,
    @ColumnInfo(name = "project_id") @SerializedName("project_id") val projectId: String? = null,
    @ColumnInfo(name = "mission_title") @SerializedName("mission_title") val missionTitle: String? = null,
    @ColumnInfo(name = "department") @SerializedName("department") val department: String? = null,
    @ColumnInfo(name = "designation") @SerializedName("designation") val designation: String? = null,
    @ColumnInfo(name = "full_name") @SerializedName("full_name") val fullName: String? = null,
    @ColumnInfo(name = "avatar") @SerializedName("avatar") val avatar: String? = null,
    @ColumnInfo(name = "mobile_no_1") @SerializedName("mobile_no_1") val mobileNo1: String? = null,
    @ColumnInfo(name = "mobile_no_2") @SerializedName("mobile_no_2") val mobileNo2: String? = null,
    @ColumnInfo(name = "email_id") @SerializedName("email_id") val emailId: String? = null,
)

@Entity(tableName = "headquarter_table")
data class EmployeeHq(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") val id: Int? = null,
    @ColumnInfo(name = "mission_title") @SerializedName("mission_title") val missionTitle: String? = null,
    @ColumnInfo(name = "department_title") @SerializedName("department_title") val deptTitle: String? = null,
    @ColumnInfo(name = "designation_title") @SerializedName("designation_title") val desgTitle: String? = null,
    @ColumnInfo(name = "full_name") @SerializedName("full_name") val fullName: String? = null,
    @ColumnInfo(name = "avatar") @SerializedName("avatar") val avatar: String? = null,
    @ColumnInfo(name = "mobile_no_1") @SerializedName("mobile_no_1") val mobileNo1: String? = null,
    @ColumnInfo(name = "mobile_no_2") @SerializedName("mobile_no_2") val mobileNo2: String? = null,
    @ColumnInfo(name = "email_id") @SerializedName("email_id") val emailId: String? = null,
)

@Entity(tableName = "emergency_table")
data class EmployeeEm(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") val id: Int? = null,
    @ColumnInfo(name = "mission_title") @SerializedName("mission_title") val missionTitle: String? = null,
    @ColumnInfo(name = "department_title") @SerializedName("department_title") val deptTitle: String? = null,
    @ColumnInfo(name = "designation_title") @SerializedName("designation_title") val desgTitle: String? = null,
    @ColumnInfo(name = "full_name") @SerializedName("full_name") val fullName: String? = null,
    @ColumnInfo(name = "avatar") @SerializedName("avatar") val avatar: String? = null,
    @ColumnInfo(name = "mobile_no_1") @SerializedName("mobile_no_1") val mobileNo1: String? = null,
    @ColumnInfo(name = "mobile_no_2") @SerializedName("mobile_no_2") val mobileNo2: String? = null,
    @ColumnInfo(name = "email_id") @SerializedName("email_id") val emailId: String? = null,
)
