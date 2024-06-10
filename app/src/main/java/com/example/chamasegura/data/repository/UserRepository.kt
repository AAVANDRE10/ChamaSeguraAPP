package com.example.chamasegura.data.repository

import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    fun getUsers(onResult: (List<User>?) -> Unit) {
        RetrofitInstance.api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun createUser(user: User, onResult: (User?) -> Unit) {
        RetrofitInstance.api.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }
}