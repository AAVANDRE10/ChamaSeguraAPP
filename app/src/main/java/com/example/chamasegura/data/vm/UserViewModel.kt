package com.example.chamasegura.data.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
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
}