package com.example.chamasegura.data.repository

import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    fun signIn(email: String, password: String, onResult: (User?) -> Unit) {
        val user = User(id = 0, name = "", email = email, password = password, photo = null, type = UserType.REGULAR, createdAt = "", updatedAt = "")
        RetrofitInstance.api.signIn(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun signUp(user: User, onResult: (User?) -> Unit) {
        RetrofitInstance.api.signUp(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }
}