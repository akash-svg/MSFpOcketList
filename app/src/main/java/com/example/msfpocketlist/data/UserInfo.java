package com.example.msfpocketlist.data;

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
        @SerializedName("mission")
        @Expose
        public String mission;
        @SerializedName("pocket")
        @Expose
        public String pocket;
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
        @SerializedName("mobile_no")
        @Expose
        public String mobileNo;
        @SerializedName("email_id")
        @Expose
        public String emailId;
        @SerializedName("device_token")
        @Expose
        public String deviceToken;
    }
}
