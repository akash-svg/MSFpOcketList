package com.msfpocketlist.ui.pocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.PocketModel;
import com.msfpocketlist.data.Project;
import com.msfpocketlist.databinding.ActivityPocketBinding;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.employee.EmployeeActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PocketActivity extends AppCompatActivity implements PocketAdapter.OnItemClick, NetworkReceiver.ConnectivityReceiverListener {
    ActivityPocketBinding binding;
    int missionId = -2;
    ApiInterface apiInterface;
    PocketRepository repository;
    List<Project> projectList = new ArrayList<>();
    PocketAdapter pocketAdapter;
    PocketAdapter.OnItemClick onItemClick;
    NetworkReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPocketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Projects");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get intent data
        if (getIntent() !=null){
            missionId = getIntent().getIntExtra("missionId",-1);
            Log.e("onCreate: ", missionId+" ");
        }
        //repo and retrofit
        onItemClick = this;
        repository = new PocketRepository(getApplication(),missionId);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //setup recycler view
        binding.pocketRec.setLayoutManager(new LinearLayoutManager(this));
        binding.pocketRec.setHasFixedSize(true);
        pocketAdapter = new PocketAdapter(projectList,this,onItemClick);
        binding.pocketRec.setAdapter(pocketAdapter);

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData(int missionId) {
        binding.pocketRec.showShimmer();
        projectList.clear();
        try {
            List<Project> a = repository.getAllPocket();
            if (a.size() !=0){
                binding.noDataFound.setVisibility(View.GONE);
                projectList.addAll(a);
                pocketAdapter.notifyDataSetChanged();
            }else{
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            binding.pocketRec.hideShimmer();
        }
    }

    private void getPocketData(int missionId) {
        binding.pocketRec.showShimmer();
        Call<PocketModel> call = apiInterface.getAllPocketList(Constant.APIKEY, missionId);
        call.enqueue(new Callback<PocketModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<PocketModel> call, @NonNull Response<PocketModel> response) {
                binding.pocketRec.hideShimmer();
                try {
                    PocketModel pocketModel = response.body();
                    if (pocketModel != null) {
                        projectList.clear();
                        if (pocketModel.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            projectList.addAll(pocketModel.projects);
                            pocketAdapter.notifyDataSetChanged();
                        } else {
                            getCacheData(missionId);
                        }
                    } else {
                        getCacheData(missionId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PocketModel> call, @NonNull Throwable t) {
                binding.pocketRec.hideShimmer();
                Toast.makeText(PocketActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                List<Project> filteredList = new ArrayList<>();
                for (Project data : projectList) {
                    if (data.title.toLowerCase().contains(newText.toString())) {
                        filteredList.add(data);
                    }
                    pocketAdapter.searchList(filteredList);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPocketListClick(Project pocket) {
        startActivity(new Intent(PocketActivity.this, EmployeeActivity.class)
                .putExtra("pocketId",pocket.id)
                .putExtra("missionId",pocket.mission));
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (missionId !=-1 && missionId !=-2 ){
            if (isConnected) {
                binding.con.setVisibility(View.GONE);
                getPocketData(missionId);
            } else {
                getCacheData(missionId);
                binding.con.setVisibility(View.VISIBLE);
            }
        }else{
            Toast.makeText(this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
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
}