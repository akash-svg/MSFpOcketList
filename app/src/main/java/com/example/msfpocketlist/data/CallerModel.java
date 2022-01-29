package com.example.msfpocketlist.data;

public class CallerModel {
     private String conId;
     private String incoming;


    public CallerModel(String conId, String incoming) {
        this.conId = conId;
        this.incoming = incoming;
    }

    public CallerModel() {

    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getIncoming() {
        return incoming;
    }

    public void setIncoming(String incoming) {
        this.incoming = incoming;
    }
}
