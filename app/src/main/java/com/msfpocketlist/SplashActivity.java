package com.msfpocketlist;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.msfpocketlist.ui.auth.login.LoginActivity;
import com.msfpocketlist.ui.dashboard.HomeActivity;
import com.msfpocketlist.util.DataManager;


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