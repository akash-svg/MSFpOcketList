package com.example.msfpocketlist.ui.dashboard.account;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.msfpocketlist.BuildConfig;
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.common.Constant;
import com.example.msfpocketlist.data.UserInfo;
import com.example.msfpocketlist.remote.ApiClient;
import com.example.msfpocketlist.remote.ApiInterface;
import com.example.msfpocketlist.ui.auth.login.LoginActivity;
import com.example.msfpocketlist.ui.dashboard.HomeActivity;
import com.example.msfpocketlist.util.ConnectionManager;
import com.example.msfpocketlist.util.DataManager;
import com.example.msfpocketlist.util.SessionManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vmadalin.easypermissions.EasyPermissions;
import com.vmadalin.easypermissions.dialogs.SettingsDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AccountFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    View view;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    CircleImageView userImg;
    TextView userName,userDesignation,appVersion;
    TextInputEditText userPhone,userEmail,userAddress;
    Button logoutBtn;
    File output = null;
    String imagePath = "";
    Uri uri;
    ApiInterface apiInterface;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account");

        userImg = view.findViewById(R.id.userImg);
        userName = view.findViewById(R.id.userNameEd);
        userDesignation = view.findViewById(R.id.userDesignation);
        appVersion = view.findViewById(R.id.appVersion);
        userPhone = view.findViewById(R.id.userPhone);
        userEmail = view.findViewById(R.id.userMail);
        userAddress = view.findViewById(R.id.userAddress);
        logoutBtn = view.findViewById(R.id.logout_btn);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        setUpUserProfile();

        //logout event listener
        logoutBtn.setOnClickListener(v->{
            SessionManager.logout(requireContext());
        });

        //profile image upload event listener
        userImg.setOnClickListener(v->{
            if (hasPermission()){
                showImageSelection();
            }else{
                requestPermission();
            }
        });

        return view;
    }

    private void setUpUserProfile() {
        UserInfo.Profile profileData = DataManager.getInstance().getUserData(requireContext()).profile;

        Glide.with(requireContext())
                .load(Constant.IMAGE_PATH+profileData.avatar)
                .apply(new RequestOptions().placeholder(R.drawable.ic_user))
                .into(userImg);
        userName.setText(profileData.fullName);
        userDesignation.setText(profileData.designation);
        userPhone.setText(profileData.mobileNo);
        userEmail.setText(profileData.emailId);
        userAddress.setText(profileData.mission);
        appVersion.setText("Version "+BuildConfig.VERSION_NAME);
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new SettingsDialog.Builder(requireActivity()).build().show();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        //open image picker
        showImageSelection();
    }

    private Boolean hasPermission() {
       return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA);
    }
    private void requestPermission() {
        EasyPermissions.requestPermissions(this,
                "This application cannot work without Location Permission.",
                PERMISSION_LOCATION_REQUEST_CODE, Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }


    //image related function
    public void showImageSelection() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.image_selection_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        ImageView cameraBtn = dialog.findViewById(R.id.cameraBtn);
        ImageView galleryBtn = dialog.findViewById(R.id.galleryBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                try {
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openGallery();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select a Image"), SELECT_FILE);
    }

    private void openCamera() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output = createImageFile();
        uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", output);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "cachedImage";
        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg",getActivity().getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e(e.toString());
        }
        Timber.e("%s ", image.exists());
        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Timber.e(imagePath);
                openCropImageActivity(imagePath);
            } else if (requestCode == SELECT_FILE) {
                CropImage.activity(data.getData()).start(requireContext(),this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imagePath = DataManager.getPathFromUri(requireContext(), result.getUri());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                userImg.setImageBitmap(bitmap);
                Timber.e(imagePath);

                //run image upload function
                if (imagePath !=null && !imagePath.equalsIgnoreCase("")){
                    if (ConnectionManager.connection(requireContext())){
                        updateUserProfile(imagePath);
                    }else {
                        Toast.makeText(requireContext(), "Please Connect to Internet!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private void openCropImageActivity(String str_image_path) {
        CropImage.activity(Uri.fromFile(new File(str_image_path)))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(),this);
    }

    private void updateUserProfile(String imagePath) {
        MultipartBody.Part filePart;
        File file = new File(imagePath);
        filePart = MultipartBody.Part.createFormData("avatar", file.getName(), RequestBody.create(file, MediaType.parse("image/*")));
        int id = DataManager.getInstance().getUserData(requireContext()).profile.id;
        DataManager.getInstance().showProgressMessage(requireActivity());
        Call<UserInfo> call = apiInterface.updateProfile(Constant.APIKEY,id,filePart);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                DataManager.getInstance().hideProgressMessage();
                UserInfo userInfo = response.body();
                if (userInfo != null) {
                    if (userInfo.response == 200) {
                        String dataResponse = new Gson().toJson(response.body());
                        SessionManager.writeString(requireContext(), Constant.USER_INFO, dataResponse);
                        Toast.makeText(requireContext(), "Profile Picture updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), userInfo.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "error occurred try again!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}