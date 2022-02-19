package com.msfpocketlist.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.msfpocketlist.data.Mission;

import java.util.List;

public class MissionModel {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Integer response;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("missions")
    @Expose
    public List<Mission> missions = null;
}
