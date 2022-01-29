package com.example.msfpocketlist.ui.pocket;

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

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.Pocket;
import com.example.msfpocketlist.data.PocketModel;
import com.example.msfpocketlist.databinding.ActivityPocketBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.dashboard.mission.MissionAdapter;
import com.example.msfpocketlist.ui.employee.EmployeeActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class PocketActivity extends AppCompatActivity implements PocketAdapter.OnItemClick {
    ActivityPocketBinding binding;
    int missionId = -2;
    ApiInterface apiInterface;
    PocketRepository repository;
    List<Pocket> pocketList = new ArrayList<>();
    PocketAdapter pocketAdapter;
    PocketAdapter.OnItemClick onItemClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPocketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pockets");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() !=null){
            missionId = getIntent().getIntExtra("missionId",-1);
            Timber.e("onCreate: %s", missionId);
        }
        onItemClick = this;
        repository = new PocketRepository(getApplication(),missionId);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        binding.pocketRec.setLayoutManager(new LinearLayoutManager(this));
        binding.pocketRec.setHasFixedSize(true);
        pocketAdapter = new PocketAdapter(pocketList,this,onItemClick);
        binding.pocketRec.setAdapter(pocketAdapter);
        if (missionId !=-1 && missionId !=-2 ){
            if (ConnectionManager.connection(this)) {
                getPocketData(missionId);
            } else {
                getCacheData(missionId);
            }
        }else{
            Toast.makeText(this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCacheData(int missionId) {
        pocketList.clear();
        try {
            List<Pocket> a = repository.getAllPocket();
            Timber.e("getCacheData: %s", a.toString());
            if (a.size() !=0){
                binding.noDataFound.setVisibility(View.GONE);
                pocketList.addAll(a);
                pocketAdapter.notifyDataSetChanged();
            }else{
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.noDataFound.setText(getString(R.string.no_data_found));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getPocketData(int missionId) {
        DataManager.getInstance().showProgressMessage(this);
        Call<PocketModel> call = apiInterface.getAllPocketList(Constant.APIKEY, missionId);
        call.enqueue(new Callback<PocketModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<PocketModel> call, @NonNull Response<PocketModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    PocketModel pocketModel = response.body();
                    if (pocketModel != null) {
                        pocketList.clear();
                        if (pocketModel.response == 200) {
                            binding.noDataFound.setVisibility(View.GONE);
                            pocketList.addAll(pocketModel.pockets);
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
                DataManager.getInstance().hideProgressMessage();
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
                List<Pocket> filteredList = new ArrayList<>();
                for (Pocket data : pocketList) {
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
    public void onPocketListClick(Pocket pocket) {
        startActivity(new Intent(PocketActivity.this, EmployeeActivity.class)
                .putExtra("pocketId",pocket.id)
                .putExtra("missionId",pocket.mission));
        finish();
    }

}