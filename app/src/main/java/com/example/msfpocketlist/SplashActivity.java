package com.example.msfpocketlist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.msfpocketlist.ui.auth.login.LoginActivity;
import com.example.msfpocketlist.ui.dashboard.HomeActivity;
import com.example.msfpocketlist.util.DataManager;
import com.example.msfpocketlist.util.SessionManager;

import timber.log.Timber;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        if (getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }



        new Handler().postDelayed(() -> {
            if (DataManager.getInstance().getUserData(this) !=null && DataManager.getInstance().getUserData(this).profile !=null){
                startActivity(new Intent(this, HomeActivity.class));
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, SPLASH_SCREEN);
    }
}