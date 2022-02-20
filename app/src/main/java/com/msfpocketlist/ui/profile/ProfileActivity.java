package com.msfpocketlist.ui.profile;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.employee.EmployeeActivity;
import com.msfpocketlist.util.DataManager;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener, EasyPermissions.PermissionCallbacks {
    int userId, missionId, pocketId;
    ApiInterface apiInterface;
    ProfileRepository repository;
    NetworkReceiver receiver;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;
    LinearLayout infoLay;
    CircleImageView profileImg;
    TextView userName, designation, mission, email, phoneOne, phoneTwo, noDataFound;
    ImageView phoneTwoLay, phoneOneLay, emailLay;
    View con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        //init views
        infoLay = findViewById(R.id.infoLay);
        profileImg = findViewById(R.id.profileImg);
        userName = findViewById(R.id.userName);
        designation = findViewById(R.id.designation);
        mission = findViewById(R.id.mission);
        email = findViewById(R.id.email);
        phoneOne = findViewById(R.id.phoneOne);
        phoneTwo = findViewById(R.id.phoneTwo);
        noDataFound = findViewById(R.id.noDataFound);
        phoneOneLay = findViewById(R.id.phoneOneLay);
        phoneTwoLay = findViewById(R.id.phoneTwoLay);
        emailLay = findViewById(R.id.emailLay);
        con = findViewById(R.id.con);

        //retrofit
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        repository = new ProfileRepository(getApplication());

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);

        emailLay.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email.getText().toString()));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        });

        phoneOneLay.setOnClickListener(v -> {
            if (hasCallPermission()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneOne.getText().toString()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                requestCallPermission();
            }
        });


        phoneTwoLay.setOnClickListener(v -> {
            if (hasCallPermission()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneTwo.getText().toString()));
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
            if (localData != null) {
                noDataFound.setVisibility(View.GONE);
                setUserDataLocal(localData);
            } else {
                infoLay.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
                noDataFound.setText(getString(R.string.no_data_found));
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                            noDataFound.setVisibility(View.GONE);
                            UserInfo.Profile data = userInfo.profile;
                            setUserData(data);
                        } else {
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
                noDataFound.setVisibility(View.VISIBLE);
                noDataFound.setText(getString(R.string.no_data_found));
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

    @SuppressLint("SetTextI18n")
    private void setUserData(UserInfo.Profile data) {
        Glide.with(ProfileActivity.this)
                .load(Constant.IMAGE_PATH + data.avatar)
                .apply(new RequestOptions().placeholder(R.drawable.ic_user))
                .into(profileImg);
        userName.setText(data.fullName);
        designation.setText(data.designation + ", " + data.department);
        mission.setText(data.missionTitle);
        if (data.emailId == null) {
            emailLay.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        } else {
            email.setText(data.emailId);
        }

        if (data.mobileNo1 == null) {
            phoneOneLay.setVisibility(View.GONE);
            phoneOne.setVisibility(View.GONE);
        } else {
            phoneOne.setText(data.mobileNo1);
        }

        if (data.mobileNo2 == null) {
            phoneTwoLay.setVisibility(View.GONE);
            phoneTwo.setVisibility(View.GONE);
        } else {
            phoneTwo.setText(data.mobileNo2);
        }

    }


    @SuppressLint("SetTextI18n")
    private void setUserDataLocal(EmployeeAll data) {
        Glide.with(ProfileActivity.this)
                .load(Constant.IMAGE_PATH + data.avatar)
                .apply(new RequestOptions().placeholder(R.drawable.ic_user))
                .into(profileImg);
        userName.setText(data.fullName);
        designation.setText(data.designation + ", " + data.department);
        mission.setText(data.missionTitle);
        if (data.emailId == null) {
            emailLay.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        } else {
            email.setText(data.emailId);
        }

        if (data.mobileNo1 == null) {
            phoneOneLay.setVisibility(View.GONE);
            phoneOne.setVisibility(View.GONE);
        } else {
            phoneOne.setText(data.mobileNo1);
        }

        if (data.mobileNo2 == null) {
            phoneTwoLay.setVisibility(View.GONE);
            phoneTwo.setVisibility(View.GONE);
        } else {
            phoneTwo.setText(data.mobileNo2);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            con.setVisibility(View.GONE);
            getUserDetail(userId);
        } else {
            con.setVisibility(View.VISIBLE);
            getCacheData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, BaseClass.intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


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
}