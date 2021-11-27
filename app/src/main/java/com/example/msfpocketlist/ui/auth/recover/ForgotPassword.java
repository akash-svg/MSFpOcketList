package com.example.msfpocketlist.ui.auth.recover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.msfpocketlist.ui.hq.HomeActivity;
import com.example.msfpocketlist.R;

public class ForgotPassword extends AppCompatActivity {
    Button nexe2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        nexe2=findViewById(R.id.btn_next_2);

        nexe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}