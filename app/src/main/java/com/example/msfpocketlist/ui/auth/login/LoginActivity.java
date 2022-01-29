package com.example.msfpocketlist.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.UserInfo;
import com.example.msfpocketlist.databinding.ActivityLoginBinding;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.dashboard.HomeActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.example.msfpocketlist.util.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String token = "";
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                try {
                    token = Objects.requireNonNull(task.getResult()).toString();
                    Timber.e("onComplete: %s", token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
                if (ConnectionManager.connection(this)){
                    userLogin(binding.userEmail.getText().toString(), binding.loginPass.getText().toString());
                }else{
                    Toast.makeText(this, "Please Connect to Internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void userLogin(String phoneNumber, String password) {
        DataManager.getInstance().showProgressMessage(this);
        if (ConnectionManager.connection(this)) {
            token = token != null ? token : "someNullToken";
            Timber.e("userLogin: %s", token);
            Call<UserInfo> call = apiInterface.userLogin(Constant.APIKEY, binding.userEmail.getText().toString(), binding.loginPass.getText().toString(), token);
            call.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                    DataManager.getInstance().hideProgressMessage();
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        if (userInfo.response == 200) {
                            String dataResponse = new Gson().toJson(response.body());
                            Timber.e("LOGIN RESPONSE %s", dataResponse);
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
}