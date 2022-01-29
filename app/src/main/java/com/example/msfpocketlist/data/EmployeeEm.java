package com.example.msfpocketlist.data;

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

    @ColumnInfo(name = "mobile_no")
    @SerializedName("mobile_no")
    @Expose
    public String mobileNo;

    @ColumnInfo(name = "email_id")
    @SerializedName("email_id")
    @Expose
    public String emailId;

    @ColumnInfo(name = "dept_title")
    @SerializedName("dept_title")
    @Expose
    public String deptTitle;

    @ColumnInfo(name = "desg_title")
    @SerializedName("desg_title")
    @Expose
    public String desgTitle;
}
