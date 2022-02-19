package com.msfpocketlist.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.msfpocketlist.SplashActivity;

public class SessionManager {
    public static final String PREF_NAME = "PocketList";
    public static final int MODE = Context.MODE_PRIVATE;

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public void deleteUser(Context context) {
        getEditor(context).clear().commit();
    }

    public static void logout(Context context) {
        final Activity activity = (Activity) context;
        getEditor(context).clear().commit();
        Toast.makeText(context, "Successfully logout", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(activity, SplashActivity.class));
        activity.finish();
    }

}

