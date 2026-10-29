package com.msfpocketlist.remote

import com.msfpocketlist.common.Constant
import com.msfpocketlist.data.EmergencyModel
import com.msfpocketlist.data.HqModel
import com.msfpocketlist.data.MissionModel
import com.msfpocketlist.data.PocketEmModel
import com.msfpocketlist.data.PocketModel
import com.msfpocketlist.data.UserInfo
import com.msfpocketlist.data.UserInfoOne
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiInterface {
    @FormUrlEncoded
    @POST("user-management/employees/login")
    suspend fun userLogin(
        @Header("Authorization") auth: String,
        @Field("email_id") emailId: String,
        @Field("password") password: String,
        @Field("device_token") deviceToken: String,
    ): UserInfo

    @Multipart
    @POST("user-management/employees/profile/{userId}/avatar")
    suspend fun updateProfile(@Header("Authorization") auth: String, @Path(value = "userId", encoded = true) userId: Int, @Part file: MultipartBody.Part): UserInfo

    @GET("user-management/employees/headquarters")
    suspend fun getHeadquarterList(@Header("Authorization") auth: String): HqModel

    @GET("user-management/employees/emergencies")
    suspend fun getEmergencyList(@Header("Authorization") auth: String): EmergencyModel

    @GET("mission-management/missions")
    suspend fun getAllMission(@Header("Authorization") auth: String): MissionModel

    /** Projects of one mission. */
    @GET("mission-management/missions/{missionId}")
    suspend fun getAllPocketList(@Header("Authorization") auth: String, @Path("missionId") missionId: Int): PocketModel

    @GET("mission-management/projects/{projectId}/employees")
    suspend fun getEmployeeOnPocket(@Header("Authorization") auth: String, @Path("projectId") projectId: Int): PocketEmModel

    @GET("user-management/employees/profile/{userId}")
    suspend fun getDetailById(@Header("Authorization") auth: String, @Path("userId") userId: Int): UserInfo

    @GET("mission-management/projects/")
    suspend fun getActivePockets(@Header("Authorization") auth: String): PocketModel

    @GET("user-management/employees")
    suspend fun getAllEmployee(@Header("Authorization") auth: String): UserInfoOne
}

fun createApi(): ApiInterface {
    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    return Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)
}
