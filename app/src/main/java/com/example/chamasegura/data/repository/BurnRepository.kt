package com.example.chamasegura.data.repository

import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.Burn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BurnRepository {

    fun getBurns(onResult: (List<Burn>?) -> Unit) {
        RetrofitInstance.api.getBurns().enqueue(object : Callback<List<Burn>> {
            override fun onResponse(call: Call<List<Burn>>, response: Response<List<Burn>>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<Burn>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun createBurn(burn: Burn, onResult: (Burn?) -> Unit) {
        RetrofitInstance.api.createBurn(burn).enqueue(object : Callback<Burn> {
            override fun onResponse(call: Call<Burn>, response: Response<Burn>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<Burn>, t: Throwable) {
                onResult(null)
            }
        })
    }
}