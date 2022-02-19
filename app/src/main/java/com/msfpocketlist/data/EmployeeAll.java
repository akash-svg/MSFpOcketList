package com.msfpocketlist.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "all_employee")
public class EmployeeAll {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    public Integer id;
    @ColumnInfo(name = "mission_id")
    @SerializedName("mission_id")
    @Expose
    public Integer missionId;
    @ColumnInfo(name = "mission_title")
    @SerializedName("mission_title")
    @Expose
    public String missionTitle;
    @ColumnInfo(name = "project_id")
    @SerializedName("project_id")
    @Expose
    public Integer projectId;
    @ColumnInfo(name = "project_title")
    @SerializedName("project_title")
    @Expose
    public String projectTitle;
    @ColumnInfo(name = "department")
    @SerializedName("department")
    @Expose
    public String department;
    @ColumnInfo(name = "designation")
    @SerializedName("designation")
    @Expose
    public String designation;
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
    @ColumnInfo(name = "device_token")
    @SerializedName("device_token")
    @Expose
    public String deviceToken;
}
