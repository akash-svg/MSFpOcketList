package com.msfpocketlist.ui.dashboard.emergency;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.EmergencyModel;
import com.msfpocketlist.data.EmployeeEm;
import com.msfpocketlist.databinding.FragmentEmergencyBinding;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.profile.ProfileActivity;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmergencyFragment extends Fragment implements EasyPermissions.PermissionCallbacks, EmergencyAdapter.OnItemClick, NetworkReceiver.ConnectivityReceiverListener {
    FragmentEmergencyBinding binding;
    EmergencyAdapter emergencyAdapter;
    ApiInterface apiInterface;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;
    EmergencyRepository repository;
    EmergencyAdapter.OnItemClick onItemClick;
    List<EmployeeEm> employeeEmList = new ArrayList<>();
    NetworkReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public EmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmergencyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //header
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Emergency");

        //repo and retrofit
        repository = new EmergencyRepository(getActivity().getApplication());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        onItemClick = this;

        //setup recycler view
        binding.emergencyRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.emergencyRec.setHasFixedSize(true);
        emergencyAdapter = new EmergencyAdapter(employeeEmList,requireContext(),onItemClick);
        binding.emergencyRec.setAdapter(emergencyAdapter);

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);


        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
        binding.emergencyRec.showShimmer();
        employeeEmList.clear();
        try {
            List<EmployeeEm> localData = repository.getAllEm();
            if (localData.size() !=0){
                binding.noDataFound.setVisibility(View.GONE);
                employeeEmList.addAll(localData);
                emergencyAdapter.notifyDataSetChanged();
            }else{
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(requireActivity().getString(R.string.no_data_found));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            binding.emergencyRec.hideShimmer();
        }
    }

    private void getEmployeeEmList() {
        binding.emergencyRec.showShimmer();
        Call<EmergencyModel> call = apiInterface.getEmergencyList(Constant.APIKEY);
        call.enqueue(new Callback<EmergencyModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<EmergencyModel> call, @NonNull Response<EmergencyModel> response) {
                binding.emergencyRec.hideShimmer();
                try {
                    EmergencyModel emergencyModel = response.body();
                    if (emergencyModel != null) {
                        employeeEmList.clear();
                        if (emergencyModel.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            employeeEmList.addAll(emergencyModel.employees);
                            emergencyAdapter.notifyDataSetChanged();
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
            public void onFailure(@NonNull Call<EmergencyModel> call, @NonNull Throwable t) {
                binding.emergencyRec.hideShimmer();
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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
                List<EmployeeEm> filteredList = new ArrayList<>();
                for (EmployeeEm data : employeeEmList) {
                    try {
                        if ((data.desgTitle.toLowerCase().contains(newText.toString()) || data.mobileNo1.contains(newText.toString()))
                        || (data.emailId.toLowerCase().contains(newText.toString()) || data.mobileNo2.contains(newText.toString()))) {
                            filteredList.add(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    emergencyAdapter.searchList(filteredList);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    //permission starts here
    private Boolean hasCallPermission() {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CALL_PHONE);
    }

    private void requestCallPermission() {
        EasyPermissions.requestPermissions(this, "This application cannot work without Location Permission.", PERMISSION_CALL_REQUEST_CODE, Manifest.permission.CALL_PHONE);
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new SettingsDialog.Builder(requireActivity()).build().show();
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireContext());
    }

    //permission ends here

    //item on click listener
    @Override
    public void onHqItemClick(EmployeeEm employeeEm) {
        startActivity(new Intent(requireContext(), ProfileActivity.class).putExtra("userId",employeeEm.id));
    }

    @Override
    public void onOfflineCall(EmployeeEm employeeEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeEm.mobileNo1));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onOnlineCall(EmployeeEm employeeEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeEm.mobileNo2));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onMsgOne(EmployeeEm employeeEm) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", employeeEm.mobileNo1, null)));
    }

    @Override
    public void onMsgTwo(EmployeeEm employeeEm) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", employeeEm.mobileNo2, null)));
    }

    //

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            binding.con.getRoot().setVisibility(View.GONE);
            getEmployeeEmList();
        } else {
            getCacheData();
            binding.con.getRoot().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(receiver,BaseClass.intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(receiver);
    }
}