package com.example.chamasegura.data.repository

import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.Municipality
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MunicipalityRepository {

    fun getMunicipalities(onResult: (List<Municipality>?) -> Unit) {
        RetrofitInstance.api.getMunicipalities().enqueue(object : Callback<List<Municipality>> {
            override fun onResponse(call: Call<List<Municipality>>, response: Response<List<Municipality>>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<Municipality>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun createMunicipality(municipality: Municipality, onResult: (Municipality?) -> Unit) {
        RetrofitInstance.api.createMunicipality(municipality).enqueue(object : Callback<Municipality> {
            override fun onResponse(call: Call<Municipality>, response: Response<Municipality>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<Municipality>, t: Throwable) {
                onResult(null)
            }
        })
    }
}