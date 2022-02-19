package com.msfpocketlist.ui.employee;

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
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.Employee;
import com.msfpocketlist.data.EmployeeAll;
import com.msfpocketlist.data.PocketEmModel;
import com.msfpocketlist.databinding.ActivityEmployeeBinding;
import com.msfpocketlist.localdb.AppDatabase;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.pocket.PocketActivity;
import com.msfpocketlist.ui.profile.ProfileActivity;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmployeeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EmployeeAdapter.OnItemClick, NetworkReceiver.ConnectivityReceiverListener,EmployeeCacheAdapter.OnItemClick {
    ActivityEmployeeBinding binding;
    int pocketId, missionId;
    ApiInterface apiInterface;
    List<Employee> pocketEmList = new ArrayList<>();
    List<EmployeeAll> employeeAlls = new ArrayList<>();
    EmployeeAdapter employeeAdapter;
    EmployeeCacheAdapter employeeCacheAdapter;
    EmployeeAdapter.OnItemClick onItemClick;
    EmployeeCacheAdapter.OnItemClick onItemClickOne;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    EmployeeRepository repository;
    AppDatabase db;
    NetworkReceiver receiver;
    String searchType = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get intent data
        pocketId = getIntent().getIntExtra("pocketId", -1);
        missionId = getIntent().getIntExtra("missionId", -2);

        //repo and retrofit
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        repository = new EmployeeRepository(getApplication(),missionId,pocketId);
        db = AppDatabase.getInstance(this);
        onItemClick = this;
        onItemClickOne = this;

        //setup recyclerview for network
        binding.employeeRec.setLayoutManager(new LinearLayoutManager(this));
        binding.employeeRec.setHasFixedSize(true);
        employeeAdapter = new EmployeeAdapter(pocketEmList, this, onItemClick);
        binding.employeeRec.setAdapter(employeeAdapter);
        //setup recyclerview for cache
        binding.employeeRecCache.setLayoutManager(new LinearLayoutManager(this));
        binding.employeeRecCache.setHasFixedSize(true);
        employeeCacheAdapter = new EmployeeCacheAdapter(employeeAlls, this, onItemClickOne);
        binding.employeeRecCache.setAdapter(employeeCacheAdapter);

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
        binding.employeeRec.setVisibility(View.GONE);
        binding.employeeRecCache.setVisibility(View.VISIBLE);
        binding.employeeRec.showShimmer();
        pocketEmList.clear();
        try {
            List<EmployeeAll> a = db.allEmployeeDao().getEmployeeList(pocketId);
            if (a.size() != 0) {
                searchType = "cache";
                employeeAlls.clear();
                System.out.print(a+searchType);
                binding.noDataFound.setVisibility(View.GONE);
                employeeAlls.addAll(a);
                employeeAdapter.notifyDataSetChanged();
            } else {
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            binding.employeeRec.hideShimmer();
        }
    }

    private void getEmployee() {
        if (pocketId != -1 && missionId != -2) {
            binding.employeeRec.showShimmer();
            Call<PocketEmModel> call = apiInterface.getEmployeeOnPocket(Constant.APIKEY,pocketId);
            call.enqueue(new Callback<PocketEmModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<PocketEmModel> call, @NonNull Response<PocketEmModel> response) {
                    try {
                        PocketEmModel pocketEmModel = response.body();
                        if (pocketEmModel != null) {
                            pocketEmList.clear();
                            if (pocketEmModel.response == 200) {
                                searchType = "network";
                                binding.employeeRecCache.setVisibility(View.GONE);
                                binding.employeeRec.setVisibility(View.VISIBLE);
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
                        binding.employeeRec.hideShimmer();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PocketEmModel> call, @NonNull Throwable t) {
                    binding.employeeRec.hideShimmer();
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
                if (searchType.equals("cache")){
                    List<EmployeeAll> filteredList = new ArrayList<>();
                    for (EmployeeAll data : employeeAlls) {
                        if (data.designation.toLowerCase().contains(newText.toString()) || data.mobileNo1.contains(newText.toString())) {
                            filteredList.add(data);
                        }
                        employeeCacheAdapter.searchList(filteredList);
                    }
                }else if (searchType.equals("network")){
                    List<Employee> filteredList = new ArrayList<>();
                    for (Employee data : pocketEmList) {
                        if (data.designation.toLowerCase().contains(newText.toString()) || data.mobileNo1.contains(newText.toString())) {
                            filteredList.add(data);
                        }
                        employeeAdapter.searchList(filteredList);
                    }
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
    public void onHqItemClick(Employee pocketEm) {
        startActivity(new Intent(this, ProfileActivity.class)
                .putExtra("userId", pocketEm.id)
                .putExtra("missionId", missionId)
                .putExtra("pocketId", pocketId));
        finish();
    }

    @Override
    public void onOfflineCall(Employee pocketEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + pocketEm.mobileNo1));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onOnlineCall(Employee pocketEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + pocketEm.mobileNo2));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PocketActivity.class).putExtra("missionId", missionId));
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            binding.conLay.getRoot().setVisibility(View.GONE);
            getEmployee();
        }else{
            getCacheData();
            binding.conLay.getRoot().setVisibility(View.VISIBLE);
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

    @Override
    public void onHqItemClickOne(EmployeeAll pocketEm) {
        startActivity(new Intent(this, ProfileActivity.class)
                .putExtra("userId", pocketEm.id)
                .putExtra("missionId", missionId)
                .putExtra("pocketId", pocketId));
        finish();
    }

    @Override
    public void onOfflineCallOne(EmployeeAll pocketEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + pocketEm.mobileNo1));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onOnlineCallOne(EmployeeAll pocketEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + pocketEm.mobileNo2));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }
}