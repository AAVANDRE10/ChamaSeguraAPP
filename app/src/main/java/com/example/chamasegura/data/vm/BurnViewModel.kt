package com.example.chamasegura.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.repository.BurnRepository
import kotlinx.coroutines.launch

class BurnViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = BurnRepository(application.applicationContext)
    val burns: MutableLiveData<List<Burn>> = MutableLiveData()

    fun getBurns() {
        viewModelScope.launch {
            repository.getBurns {
                burns.postValue(it)
            }
        }
    }

    fun createBurn(burn: Burn) {
        viewModelScope.launch {
            repository.createBurn(burn) {
                // Atualize o estado conforme necessário
            }
        }
    }
}
