package com.example.msfpocketlist.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.UserInfo;
import com.example.msfpocketlist.databinding.ActivityProfileBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.employee.EmployeeActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    int userId, missionId, pocketId;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            userId = getIntent().getIntExtra("userId", -1);
            missionId = getIntent().getIntExtra("missionId", -2);
            pocketId = getIntent().getIntExtra("pocketId", -3);
        }

        if (ConnectionManager.connection(this)) {
            binding.con.setVisibility(View.GONE);
            getUserDetail(userId);
        } else {
            binding.con.setVisibility(View.VISIBLE);
            binding.profileCard.setVisibility(View.GONE);
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.noDataFound.setText(getString(R.string.no_data_found));
        }
    }



    private void getUserDetail(int userId) {
        DataManager.getInstance().showProgressMessage(this);
        Call<UserInfo> call = apiInterface.getDetailById(Constant.APIKEY, userId);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        if (userInfo.response == 200) {
                            binding.profileCard.setVisibility(View.VISIBLE);
                            binding.noDataFound.setVisibility(View.GONE);
                            UserInfo.Profile data = userInfo.profile;
                            setUserData(data);
                        }
                    } else {
                        binding.profileCard.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (missionId != -2 && pocketId != -3) {
                startActivity(new Intent(this, EmployeeActivity.class)
                        .putExtra("missionId", missionId)
                        .putExtra("pocketId", pocketId));
                finish();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (missionId != -2 && pocketId != -3) {
            startActivity(new Intent(this, EmployeeActivity.class)
                    .putExtra("missionId", missionId)
                    .putExtra("pocketId", pocketId));
            finish();
        } else {
            finish();
        }
    }

    private void setUserData(UserInfo.Profile data){
        Glide.with(ProfileActivity.this)
                .load(Constant.IMAGE_PATH + data.avatar)
                .apply(new RequestOptions().placeholder(R.drawable.ic_user))
                .into(binding.profileImg);
        binding.userName.setText(data.fullName);
        binding.designation.setText(data.designation);
        binding.mission.setText(data.mission);
        binding.email.setText(data.emailId);
        binding.phone.setText(data.mobileNo);
    }
}