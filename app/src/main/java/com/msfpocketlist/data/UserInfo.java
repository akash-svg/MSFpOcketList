package com.msfpocketlist.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Integer response;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("profile")
    @Expose
    public Profile profile;

    public static class Profile {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("mission_id")
        @Expose
        public Integer missionId;
        @SerializedName("mission_title")
        @Expose
        public String missionTitle;
        @SerializedName("project_id")
        @Expose
        public Integer projectId;
        @SerializedName("project_title")
        @Expose
        public String projectTitle;
        @SerializedName("department")
        @Expose
        public String department;
        @SerializedName("designation")
        @Expose
        public String designation;
        @SerializedName("full_name")
        @Expose
        public String fullName;
        @SerializedName("avatar")
        @Expose
        public String avatar;
        @SerializedName("mobile_no_1")
        @Expose
        public String mobileNo1;
        @SerializedName("mobile_no_2")
        @Expose
        public String mobileNo2;
        @SerializedName("email_id")
        @Expose
        public String emailId;
        @SerializedName("device_token")
        @Expose
        public String deviceToken;
    }
}
