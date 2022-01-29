package com.example.msfpocketlist.util;

import com.google.gson.annotations.SerializedName;

public class PhoneModel {
    @SerializedName("conId")
    String conId;
    @SerializedName("available")
    String available;
    @SerializedName("incoming")
    String incoming;

    public PhoneModel(String conId, String available, String incoming) {
        this.conId = conId;
        this.available = available;
        this.incoming = incoming;
    }

    public PhoneModel() {
    }
}
