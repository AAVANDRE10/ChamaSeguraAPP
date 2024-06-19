package com.example.chamasegura.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.LoginResponse
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application.applicationContext)
    val user: MutableLiveData<User> = MutableLiveData()
    val users: MutableLiveData<List<User>?> = MutableLiveData()
    private var isSortedAscending = true
    private var originalUsers: List<User>? = null
    val numberOfBurnRequests = MutableLiveData<Int>()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            repository.signIn(email, password) {
                user.postValue(it)
            }
        }
    }

    fun signUp(user: User, onResult: (LoginResponse?) -> Unit) {
        viewModelScope.launch {
            repository.signUp(user, onResult)
        }
    }

    fun getUser(userId: Int): LiveData<User?> {
        return repository.getUser(userId)
    }

    fun getNumberOfBurnRequests(userId: Int) {
        viewModelScope.launch {
            val count = repository.getNumberOfBurnRequests(userId)
            numberOfBurnRequests.postValue(count)
        }
    }

    fun getUserById(userId: Int): LiveData<User?> {
        return repository.getUser(userId)
    }

    fun getAllUsers() {
        viewModelScope.launch {
            repository.getAllUsers {
                originalUsers = it
                users.postValue(it)
            }
        }
    }

    fun sortUsersByName() {
        val sortedUsers = if (isSortedAscending) {
            users.value?.sortedBy { it.name }
        } else {
            users.value?.sortedByDescending { it.name }
        }
        isSortedAscending = !isSortedAscending
        users.postValue(sortedUsers)
    }

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            users.postValue(originalUsers)
        } else {
            val filteredUsers = originalUsers?.filter {
                it.name.contains(query, true) || it.email.contains(query, true)
            }
            users.postValue(filteredUsers)
        }
    }

    fun updateUser(id: Int, user: User, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updateUser(id, user, onResult)
        }
    }

    fun updatePhoto(id: Int, photo: MultipartBody.Part, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updatePhoto(id, photo, onResult)
        }
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.changePassword(userId, oldPassword, newPassword, onResult)
        }
    }
}