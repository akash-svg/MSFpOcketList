package com.msfpocketlist.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "emergency_table")
public class EmployeeEm {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    public Integer id;
    @ColumnInfo(name = "admin_id")
    @SerializedName("admin_id")
    @Expose
    public String adminId;
    @ColumnInfo(name = "mission_id")
    @SerializedName("mission_id")
    @Expose
    public String missionId;
    @ColumnInfo(name = "mission_title")
    @SerializedName("mission_title")
    @Expose
    public String missionTitle;
    @ColumnInfo(name = "project_id")
    @SerializedName("project_id")
    @Expose
    public String projectId;
    @ColumnInfo(name = "project_title")
    @SerializedName("project_title")
    @Expose
    public String projectTitle;
    @ColumnInfo(name = "department_id")
    @SerializedName("department_id")
    @Expose
    public Integer deptId;
    @ColumnInfo(name = "department_title")
    @SerializedName("department_title")
    @Expose
    public String deptTitle;
    @ColumnInfo(name = "designation_id")
    @SerializedName("designation_id")
    @Expose
    public String desgId;
    @ColumnInfo(name = "designation_title")
    @SerializedName("designation_title")
    @Expose
    public String desgTitle;
    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @ColumnInfo(name = "avatar")
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @ColumnInfo(name = "mobile_no_1")
    @SerializedName("mobile_no_1")
    @Expose
    public String mobileNo1;
    @ColumnInfo(name = "mobile_no_2")
    @SerializedName("mobile_no_2")
    @Expose
    public String mobileNo2;
    @ColumnInfo(name = "email_id")
    @SerializedName("email_id")
    @Expose
    public String emailId;
    @ColumnInfo(name = "registered_for")
    @SerializedName("registered_for")
    @Expose
    public String registeredFor;
    @ColumnInfo(name = "account_belongs")
    @SerializedName("account_belongs")
    @Expose
    public String accountBelongs;
    @ColumnInfo(name = "device_token")
    @SerializedName("device_token")
    @Expose
    public String deviceToken;
    @ColumnInfo(name = "is_active")
    @SerializedName("is_active")
    @Expose
    public String isActive;
    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @ColumnInfo(name = "deleted_at")
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
}
