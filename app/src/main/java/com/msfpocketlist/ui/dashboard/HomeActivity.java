package com.msfpocketlist.ui.dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.msfpocketlist.R;
import com.msfpocketlist.ui.dashboard.account.AccountFragment;
import com.msfpocketlist.ui.dashboard.emergency.EmergencyFragment;
import com.msfpocketlist.ui.dashboard.hq.MSFHQFragment;
import com.msfpocketlist.ui.dashboard.mission.MissionFragment;
import com.msfpocketlist.ui.sync.SyncActivity;


public class HomeActivity extends AppCompatActivity {
     BottomNavigationView bottomNavView;
     FloatingActionButton fab;
    MSFHQFragment msfhqFragment = new MSFHQFragment();
    MissionFragment missionFragment = new MissionFragment();
    EmergencyFragment emergencyFragment = new EmergencyFragment();
    AccountFragment accountFragment = new AccountFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavView = findViewById(R.id.bottomNavView);
        fab = findViewById(R.id.fab);
        bottomNavView.setBackground(null);
        bottomNavView.getMenu().getItem(2).setEnabled(false);
        if (savedInstanceState==null){
            replaceFragment(msfhqFragment);
        }

        bottomNavView.setOnItemSelectedListener(item -> {
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
        });

        fab.setOnClickListener(v->{
            startActivity(new Intent(this, SyncActivity.class));
            overridePendingTransition(R.anim.bottom_to_top,R.anim.top_to_bottom);
        });




    }



    @Override
    public void onBackPressed() {
        if (bottomNavView.getSelectedItemId()==R.id.home){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Pocket List");
            alertDialogBuilder.setMessage("Are you sure, You want to exit");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();
                dialog.dismiss();
                finish();
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
                dialog.dismiss();
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            replaceFragment(msfhqFragment);
            bottomNavView.setSelectedItemId(R.id.home);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }

}