package com.msfpocketlist.remote;



import com.msfpocketlist.data.EmergencyModel;
import com.msfpocketlist.data.HqModel;
import com.msfpocketlist.data.MissionModel;
import com.msfpocketlist.data.PocketEmModel;
import com.msfpocketlist.data.PocketModel;
import com.msfpocketlist.data.UserInfo;
import com.msfpocketlist.data.UserInfoOne;

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
    @POST("user-management/employee/login")
    Call<UserInfo> userLogin(@Header("Authorization") String auth, @Field("email_id") String emailId, @Field("password") String password, @Field("device_token") String deviceToken);

    //profile update
    @Multipart
    @POST("user-management/employee/profile/{userId}/update")
    Call<UserInfo> updateProfile(@Header("Authorization") String auth,@Path(value = "userId",encoded = true) int userId,@Part MultipartBody.Part file);

    //get head quarter employee list
    @GET("user-management/employee/headquarters")
    Call<HqModel> getHeadquarterList(@Header("Authorization") String auth);

    //get emergency employee list
    @GET("user-management/employee/emergencies")
    Call<EmergencyModel> getEmergencyList(@Header("Authorization") String auth);

    //get mission list
    @GET("mission-management/missions")
    Call<MissionModel> getAllMission(@Header("Authorization") String auth);

    //get project list by mission id
    @GET("mission-management/missions/{missionId}")
    Call<PocketModel> getAllPocketList(@Header("Authorization") String auth, @Path("missionId") int missionId);

    //employee on projects
    @GET("mission-management/projects/{projectId}")
    Call<PocketEmModel> getEmployeeOnPocket(@Header("Authorization") String auth,@Path("projectId") int pocketId);

    //get employee detail by id
    @GET("user-management/employee/profile/{userId}")
    Call<UserInfo> getDetailById(@Header("Authorization") String auth,@Path("userId") int userId);

    //get all active projects
    @GET("mission-management/projects/")
    Call<PocketModel> getActivePockets(@Header("Authorization") String auth);

    //get all employee list
    @GET("user-management/employees")
    Call<UserInfoOne> getAllEmployee(@Header("Authorization") String auth);
}
