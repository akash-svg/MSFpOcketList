package com.msfpocketlist.ui.dashboard.hq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.EmployeeHq;
import com.msfpocketlist.data.HqModel;
import com.msfpocketlist.databinding.FragmentHqBinding;
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


public class MSFHQFragment extends Fragment implements EasyPermissions.PermissionCallbacks, HQAdapter.OnItemClick, NetworkReceiver.ConnectivityReceiverListener{
    FragmentHqBinding binding;
    ApiInterface apiInterface;
    List<EmployeeHq> employeeHqList = new ArrayList<>();
    HQAdapter hqAdapter;
    HQAdapter.OnItemClick onItemClick;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;
    HeadQuarterRepository repository;
    NetworkReceiver receiver;
    public MSFHQFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHqBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //header
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("MSF HQ");

        //repo and retrofit
        repository = new HeadQuarterRepository(getActivity().getApplication());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        onItemClick = this;

        //setup recycler view
        binding.hqRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.hqRec.setHasFixedSize(true);
        hqAdapter = new HQAdapter(employeeHqList, requireContext(), onItemClick);
        binding.hqRec.setAdapter(hqAdapter);

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);


        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
        binding.hqRec.showShimmer();
        employeeHqList.clear();
        try {
            List<EmployeeHq> localData = repository.getAllHq();
            if (localData.size() != 0) {
                binding.noDataFound.setVisibility(View.GONE);
                employeeHqList.addAll(localData);
                hqAdapter.notifyDataSetChanged();
            } else {
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(requireActivity().getString(R.string.no_data_found));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            binding.hqRec.hideShimmer();
        }

    }

    private void getEmployeeHqList() {
        binding.hqRec.showShimmer();
        Call<HqModel> call = apiInterface.getHeadquarterList(Constant.APIKEY);
        call.enqueue(new Callback<HqModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<HqModel> call, @NonNull Response<HqModel> response) {
                binding.hqRec.hideShimmer();
                try {
                    HqModel hqModel = response.body();
                    if (hqModel != null) {
                        employeeHqList.clear();
                        if (hqModel.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            employeeHqList.addAll(hqModel.employees);
                            hqAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), hqModel.message, Toast.LENGTH_SHORT).show();
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
            public void onFailure(@NonNull Call<HqModel> call, @NonNull Throwable t) {
                binding.hqRec.hideShimmer();
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
                List<EmployeeHq> filteredList = new ArrayList<>();
                for (EmployeeHq data : employeeHqList) {
                    if (data.desgTitle.toLowerCase().contains(newText.toString()) || data.mobileNo1.contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    hqAdapter.searchList(filteredList);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    //permission related function starts here
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

    //permission related function ends here



    //item on click event listener

    @Override
    public void onHqItemClick(EmployeeHq employeeHq) {
        startActivity(new Intent(requireActivity(), ProfileActivity.class).putExtra("userId", employeeHq.id));
    }
    @Override
    public void onOfflineCall(EmployeeHq employeeHq) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeHq.mobileNo1));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }

    @Override
    public void onOnlineCall(EmployeeHq employeeHq) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeHq.mobileNo2));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            requestCallPermission();
        }
    }



    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
            binding.con.getRoot().setVisibility(View.GONE);
            getEmployeeHqList();
        }else{
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