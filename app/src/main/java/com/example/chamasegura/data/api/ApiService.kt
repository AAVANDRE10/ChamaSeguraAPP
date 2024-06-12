package com.example.chamasegura.data.api

import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.entities.Municipality
import com.example.chamasegura.data.entities.User
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @POST("auth/signin")
    fun signIn(@Body user: User): Call<User>

    @POST("auth/signup")
    fun signUp(@Body user: User): Call<User>

    @GET("burns")
    fun getBurns(): Call<List<Burn>>

    @GET("burns/{number}")
    fun getBurnById(@Path("number") number: Int): Call<Burn>

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
