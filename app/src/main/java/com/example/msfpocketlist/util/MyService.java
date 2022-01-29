package com.example.msfpocketlist.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.msfpocketlist.ui.call.CallActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class MyService extends Service {
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("user");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        String userPhone = DataManager.getInstance().getUserData(this).profile.mobileNo;
        dbRef.child(userPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child("incoming").getValue(String.class) !=null){
                        Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                        intent.putExtra("callerId", snapshot.child("incoming").getValue(String.class));
                        intent.putExtra("conId", snapshot.child("conId").getValue(String.class));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Timber.e("service started...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.e("service destroy...");
    }
}
