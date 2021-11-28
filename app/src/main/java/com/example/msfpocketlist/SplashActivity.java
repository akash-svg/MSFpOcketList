package com.example.msfpocketlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.msfpocketlist.ui.auth.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        getSupportActionBar().hide();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, SPLASH_SCREEN);
    }
}