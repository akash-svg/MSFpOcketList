package com.example.msfpocketlist.remote;


import com.example.msfpocketlist.data.EmergencyModel;
import com.example.msfpocketlist.data.EmployeeEm;
import com.example.msfpocketlist.data.HqModel;
import com.example.msfpocketlist.data.Mission;
import com.example.msfpocketlist.data.MissionModel;
import com.example.msfpocketlist.data.PocketEmModel;
import com.example.msfpocketlist.data.PocketModel;
import com.example.msfpocketlist.data.UserInfo;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    //login
    @FormUrlEncoded
    @POST("user-management/users/login")
    Call<UserInfo> userLogin(@Header("Authorization") String auth,@Field("email_id") String emailId,@Field("password") String password,@Field("device_token") String deviceToken);

    //profile update
    @Multipart
    @POST("user-management/users/update/avatar/{userId}")
    Call<UserInfo> updateProfile(@Header("Authorization") String auth,@Path(value = "userId",encoded = true) int userId,@Part MultipartBody.Part file);

    //get head quarter employee list
    @GET("employee-management/employees/headquater")
    Call<HqModel> getHeadquarterList(@Header("Authorization") String auth);

    //get emergency employee list
    @GET("employee-management/employees/emergency")
    Call<EmergencyModel> getEmergencyList(@Header("Authorization") String auth);

    //get mission list
    @GET("mission-management/missions")
    Call<MissionModel> getAllMission(@Header("Authorization") String auth);

    //get pocket list
    @GET("mission-management/missions/{missionId}")
    Call<PocketModel> getAllPocketList(@Header("Authorization") String auth,@Path("missionId") int missionId);

    //employee on pocket
    @GET("mission-management/missions/{missionId}/pockets/{pocketId}")
    Call<PocketEmModel> getEmployeeOnPocket(@Header("Authorization") String auth, @Path("missionId") int missionId, @Path("pocketId") int pocketId);

    //get employee detail by id
    @GET("user-management/users/profile/{userId}")
    Call<UserInfo> getDetailById(@Header("Authorization") String auth,@Path("userId") int userId);

    //get all active pockets
    @GET("mission-management/pockets")
    Call<PocketModel> getActivePockets(@Header("Authorization") String auth);
}
