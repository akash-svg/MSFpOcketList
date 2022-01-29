package com.example.msfpocketlist.util.network;

public class ConnectionModel {
    boolean isConnected;
    public ConnectionModel(boolean isConnected) {
        this.isConnected = isConnected;
    }
    public boolean isConnected() { return isConnected; }
}
