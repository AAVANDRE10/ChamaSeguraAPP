package com.example.chamasegura.data.repository

import android.content.Context
import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.LoginResponse
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.utils.AuthManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(private val context: Context) {

    private val api = RetrofitInstance.create(context)
    private val authManager = AuthManager(context)

    fun signIn(email: String, password: String, onResult: (User?) -> Unit) {
        val user = User(id = 0, name = "", email = email, password = password, photo = null, type = UserType.REGULAR, createdAt = "", updatedAt = "")
        api.signIn(user).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                response.body()?.let {
                    authManager.saveAuthData(it.name, it.token)
                    onResult(User(id = 0, name = it.name, email = email, password = password, photo = null, type = UserType.REGULAR, createdAt = "", updatedAt = ""))
                } ?: onResult(null)
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun signUp(user: User, onResult: (User?) -> Unit) {
        api.signUp(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getUser(id: Int, onResult: (User?) -> Unit) {
        api.getUser(id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun updateUser(id: Int, user: User, onResult: (Boolean, String?) -> Unit) {
        api.updateUser(id, user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMessage = response.errorBody()?.string()
                    onResult(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(false, t.message)
            }
        })
    }
}