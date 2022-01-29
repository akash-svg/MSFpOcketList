package com.example.msfpocketlist.ui.dashboard.hq;

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

import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.CallerModel;
import com.example.msfpocketlist.data.EmployeeHq;
import com.example.msfpocketlist.data.HqModel;
import com.example.msfpocketlist.databinding.FragmentHqBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.profile.ProfileActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.facebook.react.modules.core.PermissionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MSFHQFragment extends Fragment implements EasyPermissions.PermissionCallbacks, HQAdapter.OnItemClick, JitsiMeetViewListener {
    FragmentHqBinding binding;
    ApiInterface apiInterface;
    List<EmployeeHq> employeeHqList = new ArrayList<>();
    HQAdapter hqAdapter;
    HQAdapter.OnItemClick onItemClick;
    private static final int PERMISSION_CALL_REQUEST_CODE = 1;
    HeadQuarterRepository repository;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    JitsiMeetConferenceOptions options;
    String calledPhone;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("MSF HQ");
        repository = new HeadQuarterRepository(getActivity().getApplication());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        onItemClick = this;
        binding.hqRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.hqRec.setHasFixedSize(true);
        hqAdapter = new HQAdapter(employeeHqList, requireContext(), onItemClick);
        binding.hqRec.setAdapter(hqAdapter);



        if (ConnectionManager.connection(requireContext())) {
            getEmployeeHqList();
        } else {
            getCacheData();
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData() {
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
        }

    }

    private void getEmployeeHqList() {
        DataManager.getInstance().showProgressMessage(requireActivity());
        Call<HqModel> call = apiInterface.getHeadquarterList(Constant.APIKEY);
        call.enqueue(new Callback<HqModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<HqModel> call, @NonNull Response<HqModel> response) {
                DataManager.getInstance().hideProgressMessage();

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
                List<EmployeeHq> filteredList = new ArrayList<>();
                for (EmployeeHq data : employeeHqList) {
                    if (data.desgTitle.toLowerCase().contains(newText.toString()) || data.mobileNo.contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    hqAdapter.searchList(filteredList);
                }
                return true;
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
    public void onHqItemClick(EmployeeHq employeeHq) {
        startActivity(new Intent(requireActivity(), ProfileActivity.class).putExtra("userId", employeeHq.id));
    }

    @Override
    public void onOfflineCall(EmployeeHq employeeHq) {
        if (hasCallPermission()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + employeeHq.mobileNo));
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
        userCalling(employeeHq.mobileNo);
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