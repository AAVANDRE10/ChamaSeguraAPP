package com.example.chamasegura.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application.applicationContext)
    val user: MutableLiveData<User> = MutableLiveData()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            repository.signIn(email, password) {
                user.postValue(it)
            }
        }
    }

    fun signUp(user: User) {
        viewModelScope.launch {
            repository.signUp(user) {
                this@UserViewModel.user.postValue(it)
            }
        }
    }

    fun getUser(id: Int) {
        viewModelScope.launch {
            repository.getUser(id) {
                user.postValue(it)
            }
        }
    }

    fun updateUser(id: Int, user: User, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updateUser(id, user, onResult)
        }
    }
}