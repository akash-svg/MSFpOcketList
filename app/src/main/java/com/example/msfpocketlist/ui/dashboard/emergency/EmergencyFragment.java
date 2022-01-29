package com.example.msfpocketlist.ui.dashboard.emergency;

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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.CallerModel;
import com.example.msfpocketlist.data.EmergencyModel;
import com.example.msfpocketlist.data.EmployeeEm;
import com.example.msfpocketlist.data.EmployeeHq;
import com.example.msfpocketlist.data.HqModel;
import com.example.msfpocketlist.databinding.FragmentEmergencyBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.dashboard.hq.HQAdapter;
import com.example.msfpocketlist.ui.dashboard.hq.HeadQuarterRepository;
import com.example.msfpocketlist.ui.profile.ProfileActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.example.msfpocketlist.util.network.ConnectionLiveData;
import com.example.msfpocketlist.util.network.ConnectionModel;
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
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmergencyFragment extends Fragment implements EasyPermissions.PermissionCallbacks,EmergencyAdapter.OnItemClick, JitsiMeetViewListener {

    FragmentEmergencyBinding binding;
    EmergencyAdapter emergencyAdapter;
    ApiInterface apiInterface;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;
    EmergencyRepository repository;
    EmergencyAdapter.OnItemClick onItemClick;
    List<EmployeeEm> employeeEmList = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String calledPhone;
    JitsiMeetConferenceOptions options;
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Emergency");
        repository = new EmergencyRepository(getActivity().getApplication());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        onItemClick = this;
        binding.emergencyRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.emergencyRec.setHasFixedSize(true);
        emergencyAdapter = new EmergencyAdapter(employeeEmList,requireContext(),onItemClick);
        binding.emergencyRec.setAdapter(emergencyAdapter);

        if (ConnectionManager.connection(requireContext())) {
            getEmployeeEmList();
        } else {
            getCacheData();
        }


        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
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
        }
    }

    private void getEmployeeEmList() {
        DataManager.getInstance().showProgressMessage(requireActivity());
        Call<EmergencyModel> call = apiInterface.getEmergencyList(Constant.APIKEY);
        call.enqueue(new Callback<EmergencyModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<EmergencyModel> call, @NonNull Response<EmergencyModel> response) {
                DataManager.getInstance().hideProgressMessage();

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
                DataManager.getInstance().hideProgressMessage();
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
                    if (data.desgTitle.toLowerCase().contains(newText.toString()) || data.mobileNo.contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    emergencyAdapter.searchList(filteredList);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

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



    @Override
    public void onHqItemClick(EmployeeEm employeeEm) {
        startActivity(new Intent(requireContext(), ProfileActivity.class).putExtra("userId",employeeEm.id));
    }

    @Override
    public void onOfflineCall(EmployeeEm employeeEm) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeEm.mobileNo));
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
        //for online calling
        userCalling(employeeEm.mobileNo);
    }

    private void userCalling(String userPhone) {
        if (DataManager.getInstance().getUserData(requireContext()).profile.mobileNo.equalsIgnoreCase(userPhone)) {
            Toast.makeText(requireContext(), "you can't call yourself", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(requireContext(), "User is occupied on other call.", Toast.LENGTH_SHORT).show();
                                } else {
                                    CallerModel callerModel = new CallerModel(String.valueOf(roomId), DataManager.getInstance().getUserData(requireContext()).profile.mobileNo);
                                    dbRef.child("user").child(userPhone).setValue(callerModel);
                                    showJetsiScreen(String.valueOf(roomId), userPhone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        CallerModel callerModel = new CallerModel(String.valueOf(roomId), DataManager.getInstance().getUserData(requireContext()).profile.mobileNo);
                        dbRef.child("user").child(userPhone).setValue(callerModel);
                        showJetsiScreen(String.valueOf(roomId), userPhone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
            JitsiMeetActivity.launch(requireContext(),options);
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