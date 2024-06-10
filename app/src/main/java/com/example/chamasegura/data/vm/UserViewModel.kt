package com.example.chamasegura.data.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    val users: MutableLiveData<List<User>> = MutableLiveData()

    fun getUsers() {
        viewModelScope.launch {
            repository.getUsers {
                users.postValue(it)
            }
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            repository.createUser(user) {
                // Atualize o estado conforme necessário
            }
        }
    }

    // Adicione outros métodos conforme necessário
}