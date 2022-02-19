package com.msfpocketlist.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.msfpocketlist.data.EmployeeHq;

import java.util.List;

public class HqModel {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Integer response;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("employees")
    @Expose
    public List<EmployeeHq> employees = null;
}
