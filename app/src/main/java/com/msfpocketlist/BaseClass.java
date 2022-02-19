package com.msfpocketlist;


import android.app.Application;
import android.content.IntentFilter;
import com.msfpocketlist.network.NetworkReceiver;

import static com.msfpocketlist.common.Constant.CONNECTIVITY_ACTION;

public class BaseClass extends Application {
    private static BaseClass mInstance;
    public static IntentFilter intentFilter;
    public static synchronized BaseClass getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
    }

    public void setConnectivityListener(NetworkReceiver.ConnectivityReceiverListener listener) {
        NetworkReceiver.connectivityReceiverListener = listener;
    }

}
