package com.example.msfpocketlist.ui.mission;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.msfpocketlist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        //BottomNavigationView bottomNavigationView;
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setBackground(null);
        bottomNavView.getMenu().getItem(2).setEnabled(false);

    }
}