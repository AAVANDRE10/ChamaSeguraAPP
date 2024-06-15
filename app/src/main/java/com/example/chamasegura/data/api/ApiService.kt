package com.example.chamasegura.data.api

import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.entities.BurnType
import com.example.chamasegura.data.entities.LoginResponse
import com.example.chamasegura.data.entities.Municipality
import com.example.chamasegura.data.entities.PasswordChangeRequest
import com.example.chamasegura.data.entities.User
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @POST("auth/signin")
    fun signIn(@Body user: User): Call<LoginResponse>

    @POST("auth/signup")
    fun signUp(@Body user: User): Call<User>

    @GET("auth/user/{id}")
    fun getUser(@Path("id") id: Int): Call<User>

    @PUT("auth/updateuser/{id}")
    fun updateUser(@Path("id") id: Int, @Body user: User): Call<User>

    @PUT("auth/changepassword/{id}")
    fun changePassword(@Path("id") userId: Int, @Header("Authorization") token: String, @Body passwordChangeRequest: PasswordChangeRequest): Call<Void>

    @GET("burns")
    fun getBurns(): Call<List<Burn>>

    @GET("burns/user/{id}")
    fun getBurnsByUser(@Path("id") userId: Int): Call<List<Burn>>

    @GET("burns/user/{userId}/type/{type}")
    fun getBurnsByUserAndType(@Path("userId") userId: Int, @Path("type") type: BurnType): Call<List<Burn>>

    @POST("burns/create")
    fun createBurn(@Body burn: Burn): Call<Burn>

    @DELETE("burns/delete/{number}")
    fun deleteBurn(@Path("number") number: Int): Call<Void>

    @GET("municipalities")
    fun getMunicipalities(): Call<List<Municipality>>

    @GET("municipalities/{number}")
    fun getMunicipalityById(@Path("number") number: Int): Call<Municipality>

    @POST("municipalities/create")
    fun createMunicipality(@Body municipality: Municipality): Call<Municipality>

    @PUT("municipalities/update/{number}")
    fun updateMunicipality(@Path("number") number: Int, @Body municipality: Municipality): Call<Municipality>

    @DELETE("municipalities/delete/{number}")
    fun deleteMunicipality(@Path("number") number: Int): Call<Void>

    @Multipart
    @PUT("photo/updatephoto/{id}")
    fun updatePhoto(@Path("id") id: Int, @Part photo: MultipartBody.Part): Call<Void>
}
