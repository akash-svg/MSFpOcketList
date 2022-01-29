package com.example.msfpocketlist.ui.call;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.example.msfpocketlist.R;
import com.example.msfpocketlist.databinding.ActivityCallBinding;
import com.example.msfpocketlist.util.DataManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

import timber.log.Timber;

public class CallActivity extends AppCompatActivity {
     ActivityCallBinding binding;
     String phoneNumber="",conId="";
     MediaPlayer mediaPlayer;
     DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
     JitsiMeetConferenceOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        mediaPlayer = MediaPlayer.create(this,R.raw.pocketlist);
        mediaPlayer.start();
        phoneNumber = getIntent().getStringExtra("callerId");
        conId = getIntent().getStringExtra("conId");

        binding.callerText.setText(phoneNumber+" is calling...");


        binding.callEndBtn.setOnClickListener(v->{
            mediaPlayer.stop();
            dbRef.child("user").child(DataManager.getInstance().getUserData(this).profile.mobileNo).setValue(null);
            finish();
        });

        binding.callReceiveBtn.setOnClickListener(v->{
            mediaPlayer.stop();
            hideallViews(conId);
        });

        binding.backBtn.setOnClickListener(v->{
            dbRef.child("user").child(DataManager.getInstance().getUserData(this).profile.mobileNo).setValue(null);
            finish();
        });
        
    }

    private void hideallViews(String conId) {
        binding.callEndBtn.setVisibility(View.GONE);
        binding.callReceiveBtn.setVisibility(View.GONE);
        binding.callerText.setVisibility(View.GONE);
        binding.shapeableImageView.setVisibility(View.GONE);
        binding.backBtn.setVisibility(View.VISIBLE);
        startCall(conId);
    }

    private void startCall(String conId) {
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(conId)
                    .setAudioOnly(true)
                    .build();
            JitsiMeetActivity.launch(this,options);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}