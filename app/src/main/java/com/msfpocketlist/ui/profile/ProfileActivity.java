package com.msfpocketlist.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.EmployeeAll;
import com.msfpocketlist.data.UserInfo;
import com.msfpocketlist.databinding.ActivityProfileBinding;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.employee.EmployeeActivity;
import com.msfpocketlist.util.DataManager;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener, EasyPermissions.PermissionCallbacks {
    ActivityProfileBinding binding;
    int userId, missionId, pocketId;
    ApiInterface apiInterface;
    ProfileRepository repository;
    NetworkReceiver receiver;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get intent data
        if (getIntent() != null) {
            userId = getIntent().getIntExtra("userId", -1);
            missionId = getIntent().getIntExtra("missionId", -2);
            pocketId = getIntent().getIntExtra("pocketId", -3);
        }
        //retrofit
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        repository = new ProfileRepository(getApplication());

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);



        binding.emailLay.setOnClickListener(v->{
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"+binding.email.getText().toString()));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        });

        binding.phoneOneLay.setOnClickListener(v->{
            if (hasCallPermission()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + binding.phoneOne.getText().toString()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                requestCallPermission();
            }
        });

        binding.phoneTwoLay.setOnClickListener(v->{
            if (hasCallPermission()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + binding.phoneTwo.getText().toString()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                requestCallPermission();
            }
        });



    }

    private void getCacheData() {
        try {
            EmployeeAll localData = repository.getAllPocket(userId);
            if (localData !=null){
                binding.shimmerFrameLayout.setVisibility(View.GONE);
                binding.noDataFound.setVisibility(View.GONE);
                setUserDataLocal(localData);
                binding.detailLay.setVisibility(View.VISIBLE);
            }else{
                binding.shimmerFrameLayout.setVisibility(View.GONE);
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            binding.shimmerFrameLayout.setVisibility(View.GONE);
        }
    }




    private void getUserDetail(int userId) {
        Call<UserInfo> call = apiInterface.getDetailById(Constant.APIKEY, userId);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        if (userInfo.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            UserInfo.Profile data = userInfo.profile;
                            setUserData(data);
                            binding.shimmerFrameLayout.setVisibility(View.GONE);
                            binding.detailLay.setVisibility(View.VISIBLE);
                        }else{
                            getCacheData();
                        }
                    } else {
                        getCacheData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                binding.shimmerFrameLayout.setVisibility(View.GONE);
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
        binding.designation.setText(data.designation+", "+data.department);
        binding.mission.setText(data.missionTitle);
        binding.email.setText(data.emailId);
        binding.phoneOne.setText(data.mobileNo1);
        binding.phoneTwo.setText(data.mobileNo2);
    }


    private void setUserDataLocal(EmployeeAll localData) {
        Glide.with(ProfileActivity.this)
                .load(Constant.IMAGE_PATH + localData.avatar)
                .apply(new RequestOptions().placeholder(R.drawable.ic_user))
                .into(binding.profileImg);
        binding.userName.setText(localData.fullName);
        binding.designation.setText(localData.designation+", "+localData.department);
        binding.mission.setText(localData.missionTitle);
        binding.email.setText(localData.emailId);
        binding.phoneOne.setText(localData.mobileNo1);
        binding.phoneTwo.setText(localData.mobileNo2);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
            binding.con.getRoot().setVisibility(View.GONE);
            getUserDetail(userId);
        }else{
            binding.con.getRoot().setVisibility(View.VISIBLE);
            getCacheData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,BaseClass.intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //permission related function starts here
    private Boolean hasCallPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE);
    }

    private void requestCallPermission() {
        EasyPermissions.requestPermissions(this, "This application cannot work without Location Permission.", PERMISSION_CALL_REQUEST_CODE, Manifest.permission.CALL_PHONE);
    }


    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new SettingsDialog.Builder(this).build().show();
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //permission related function ends here
}