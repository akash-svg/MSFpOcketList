package com.example.msfpocketlist;

import android.app.Application;
import com.droidnet.DroidNet;

import timber.log.Timber;

public class BaseClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }


}
