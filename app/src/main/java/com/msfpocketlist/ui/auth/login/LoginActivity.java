package com.msfpocketlist.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.gson.Gson;
import com.msfpocketlist.BaseClass;
import com.msfpocketlist.R;
import com.msfpocketlist.common.Constant;
import com.msfpocketlist.data.UserInfo;
import com.msfpocketlist.databinding.ActivityLoginBinding;
import com.msfpocketlist.network.NetworkReceiver;
import com.msfpocketlist.remote.ApiClient;
import com.msfpocketlist.remote.ApiInterface;
import com.msfpocketlist.ui.dashboard.HomeActivity;
import com.msfpocketlist.util.DataManager;
import com.msfpocketlist.util.SessionManager;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener{
    ActivityLoginBinding binding;
    String token = "";
    ApiInterface apiInterface;
    NetworkReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
            try {
                token = Objects.requireNonNull(task.getResult()).toString();
                Log.e("onComplete: token", token+" ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //network
        receiver = new NetworkReceiver();
        BaseClass.getInstance().setConnectivityListener(this);

        binding.btnLogin.setOnClickListener(v -> {
            if (binding.userEmail.getText().toString().equalsIgnoreCase("") || binding.userEmail.getText().toString().isEmpty()) {
                binding.userEmail.setError(getString(R.string.email_error));
                binding.userEmail.requestFocus();
            } else if (binding.loginPass.getText().toString().equalsIgnoreCase("") || binding.loginPass.getText().toString().isEmpty()) {
                binding.userEmail.clearFocus();
                binding.loginPass.setError(getString(R.string.pass_error));
                binding.loginPass.requestFocus();
            } else {
                binding.userEmail.clearFocus();
                binding.loginPass.clearFocus();
                if (NetworkReceiver.isConnected()){
                    userLogin(binding.userEmail.getText().toString(), binding.loginPass.getText().toString());
                }else{
                    Toast.makeText(this, "Please Connect to Internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void userLogin(String userEmail, String password) {
        DataManager.getInstance().showProgressMessage(this);
        if (NetworkReceiver.isConnected()) {
            token = token != null ? token : "someNullToken";
            Call<UserInfo> call = apiInterface.userLogin(Constant.APIKEY, userEmail, password, token);
            call.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                    DataManager.getInstance().hideProgressMessage();
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        if (userInfo.response == 200) {
                            String dataResponse = new Gson().toJson(response.body());
                            SessionManager.writeString(LoginActivity.this, Constant.USER_INFO, dataResponse);
                            Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, userInfo.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "error occurred try again!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                    DataManager.getInstance().hideProgressMessage();
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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