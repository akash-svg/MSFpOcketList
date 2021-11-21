package com.example.msfpocketlist;

import static com.example.msfpocketlist.R.layout.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {
    private CardView c_one,c_two,c_three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        c_one = findViewById(R.id.card_one);
        c_two = findViewById(R.id.card_two);
        c_three = findViewById(R.id.card_three);


        c_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MSF_HQ.class);
                startActivity(intent);
            }
        });


        c_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MISSION.class);
                startActivity(intent);
            }
        });

    }
}