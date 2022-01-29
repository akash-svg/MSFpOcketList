package com.example.msfpocketlist.ui.employee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.CallerModel;
import com.example.msfpocketlist.data.EmployeeHq;
import com.example.msfpocketlist.data.HqModel;
import com.example.msfpocketlist.data.Mission;
import com.example.msfpocketlist.data.PocketEm;
import com.example.msfpocketlist.data.PocketEmModel;
import com.example.msfpocketlist.databinding.ActivityEmployeeBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.dashboard.hq.HQAdapter;
import com.example.msfpocketlist.ui.pocket.PocketActivity;
import com.example.msfpocketlist.ui.profile.ProfileActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmployeeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EmployeeAdapter.OnItemClick, JitsiMeetViewListener {
    ActivityEmployeeBinding binding;
    int pocketId, missionId;
    ApiInterface apiInterface;
    List<PocketEm> pocketEmList = new ArrayList<>();
    EmployeeAdapter employeeAdapter;
    EmployeeAdapter.OnItemClick onItemClick;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    EmployeeRepository repository;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String calledPhone;
    JitsiMeetConferenceOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Employee List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pocketId = getIntent().getIntExtra("pocketId", -1);
        missionId = getIntent().getIntExtra("missionId", -2);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        repository = new EmployeeRepository(getApplication(),missionId,pocketId);
        onItemClick = this;
        binding.employeeRec.setLayoutManager(new LinearLayoutManager(this));
        binding.employeeRec.setHasFixedSize(true);
        employeeAdapter = new EmployeeAdapter(pocketEmList, this, onItemClick);
        binding.employeeRec.setAdapter(employeeAdapter);
        if (ConnectionManager.connection(this)) {
            getEmployee();
        }else{
            getCacheData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
        pocketEmList.clear();
        try {
            List<PocketEm> a = repository.getAllPocketEm();
            if (a.size() != 0) {
                binding.noDataFound.setVisibility(View.GONE);
                pocketEmList.addAll(a);
                employeeAdapter.notifyDataSetChanged();
            } else {
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEmployee() {
        if (pocketId != -1 && missionId != -2) {
            DataManager.getInstance().showProgressMessage(this);
            Call<PocketEmModel> call = apiInterface.getEmployeeOnPocket(Constant.APIKEY, missionId, pocketId);
            call.enqueue(new Callback<PocketEmModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<PocketEmModel> call, @NonNull Response<PocketEmModel> response) {
                    try {
                        PocketEmModel pocketEmModel = response.body();
                        if (pocketEmModel != null) {
                            pocketEmList.clear();
                            if (pocketEmModel.response == 200) {
                                binding.noDataFound.setVisibility(View.GONE);
                                pocketEmList.addAll(pocketEmModel.employees);
                                employeeAdapter.notifyDataSetChanged();
                                repository.insert(pocketEmList);
                            } else {
                                binding.noDataFound.setVisibility(View.VISIBLE);
                                binding.noDataFound.setText(getString(R.string.no_data_found));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        DataManager.getInstance().hideProgressMessage();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PocketEmModel> call, @NonNull Throwable t) {
                    DataManager.getInstance().hideProgressMessage();
                    Toast.makeText(EmployeeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    binding.noDataFound.setText(getString(R.string.no_data_found));
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        SearchView searchView = (SearchView) menu.getItem(0).getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<PocketEm> filteredList = new ArrayList<>();
                for (PocketEm data : pocketEmList) {
                    if (data.desgTitle.toLowerCase().contains(newText.toString()) || data.mobileNo.contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    employeeAdapter.searchList(filteredList);
                }
                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, PocketActivity.class).putExtra("missionId", missionId));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private Boolean hasCallPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE);
    }

    private void requestCallPermission() {
        EasyPermissions.requestPermissions(this, "This application cannot work without Location Permission.", PERMISSION_LOCATION_REQUEST_CODE, Manifest.permission.CALL_PHONE);
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

    @Override
    public void onHqItemClick(PocketEm pocketEm) {
        startActivity(new Intent(this, ProfileActivity.class)
                .putExtra("userId", pocketEm.id)
                .putExtra("missionId", missionId)
                .putExtra("pocketId", pocketId));
        finish();
    }

    @Override
    public void onOfflineCall(PocketEm pocketEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + pocketEm.mobileNo));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onOnlineCall(PocketEm pocketEm) {
        userCalling(pocketEm.mobileNo);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PocketActivity.class).putExtra("missionId", missionId));
        finish();
    }

    private void userCalling(String userPhone) {
        if (DataManager.getInstance().getUserData(this).profile.mobileNo.equalsIgnoreCase(userPhone)) {
            Toast.makeText(this, "you can't call yourself", Toast.LENGTH_SHORT).show();
        } else {
            Random rnd = new Random();
            int roomId = rnd.nextInt(90000000);
            dbRef.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(userPhone)) {
                        dbRef.child("user").child(userPhone).child("incoming").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Toast.makeText(EmployeeActivity.this, "User is occupied on other call.", Toast.LENGTH_SHORT).show();
                                } else {
                                    CallerModel callerModel = new CallerModel(String.valueOf(roomId), DataManager.getInstance().getUserData(EmployeeActivity.this).profile.mobileNo);
                                    dbRef.child("user").child(userPhone).setValue(callerModel);
                                    showJetsiScreen(String.valueOf(roomId), userPhone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(EmployeeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        CallerModel callerModel = new CallerModel(String.valueOf(roomId), DataManager.getInstance().getUserData(EmployeeActivity.this).profile.mobileNo);
                        dbRef.child("user").child(userPhone).setValue(callerModel);
                        showJetsiScreen(String.valueOf(roomId), userPhone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EmployeeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void showJetsiScreen(String roomId, String phone) {
        try {
            calledPhone = phone;
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(roomId)
                    .setAudioOnly(true)
                    .build();
            JitsiMeetActivity.launch(this,options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConferenceJoined(Map<String, Object> map) {

    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
        dbRef.child("user").child(calledPhone).setValue(null);
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {

    }
}