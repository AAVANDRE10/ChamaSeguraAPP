package com.example.chamasegura.data.api

import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.entities.Municipality
import com.example.chamasegura.data.entities.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.Call

interface ApiService {
    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("users")
    fun createUser(@Body user: User): Call<User>

    @GET("burns")
    fun getBurns(): Call<List<Burn>>

    @POST("burns")
    fun createBurn(@Body burn: Burn): Call<Burn>

    @GET("municipalities")
    fun getMunicipalities(): Call<List<Municipality>>

    @POST("municipalities")
    fun createMunicipality(@Body municipality: Municipality): Call<Municipality>
}
