package com.example.msfpocketlist.ui;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.msfpocketlist.R;
import com.example.msfpocketlist.ui.account.AccountFragment;
import com.example.msfpocketlist.ui.emergency.EmergencyFragment;
import com.example.msfpocketlist.ui.hq.MSFHQFragment;
import com.example.msfpocketlist.ui.mission.MissionActivity;
import com.example.msfpocketlist.ui.mission.MissionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
     BottomNavigationView bottomNavView;
     MSFHQFragment msfhqFragment = new MSFHQFragment();
     MissionFragment missionFragment = new MissionFragment();
     EmergencyFragment emergencyFragment = new EmergencyFragment();
     AccountFragment accountFragment = new AccountFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setBackground(null);
        bottomNavView.getMenu().getItem(2).setEnabled(false);
        replaceFragment(msfhqFragment);

        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int menuId = item.getItemId();

                if (menuId==R.id.home){
                    replaceFragment(msfhqFragment);
                }else if (menuId==R.id.mission){
                    replaceFragment(missionFragment);
                }else if (menuId==R.id.emergency){
                    replaceFragment(emergencyFragment);
                }else if (menuId==R.id.account){
                    replaceFragment(accountFragment);
                }

                return true;
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (bottomNavView.getSelectedItemId()==R.id.home){

            //show some alert windows for exit

            finish();
        }else{
            replaceFragment(msfhqFragment);
            bottomNavView.setSelectedItemId(R.id.home);
        }
    }
}