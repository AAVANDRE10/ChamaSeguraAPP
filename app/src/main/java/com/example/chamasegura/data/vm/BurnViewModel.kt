package com.example.chamasegura.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.api.RetrofitInstance
import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.entities.BurnType
import com.example.chamasegura.data.entities.LocationResponse
import com.example.chamasegura.data.repository.BurnRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BurnViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = BurnRepository(application.applicationContext)
    val burns: MutableLiveData<List<Burn>?> = MutableLiveData()
    val createBurnResult: MutableLiveData<Pair<Boolean, String?>> = MutableLiveData()
    private var isSortedDescending = true
    val locationInfo: MutableLiveData<LocationResponse> = MutableLiveData()

    fun getBurns() {
        viewModelScope.launch {
            repository.getBurns {
                burns.postValue(it)
            }
        }
    }

    fun getBurnsByUser(userId: Int) {
        viewModelScope.launch {
            repository.getBurnsByUser(userId) {
                burns.postValue(it)
            }
        }
    }

    fun getBurnsByUserOrderedByDate(userId: Int) {
        viewModelScope.launch {
            repository.getBurnsByUser(userId) {
                val sortedBurns = if (isSortedDescending) {
                    it?.sortedByDescending { burn -> burn.date }
                } else {
                    it?.sortedBy { burn -> burn.date }
                }
                isSortedDescending = !isSortedDescending
                burns.postValue(sortedBurns)
            }
        }
    }

    fun getBurnsByUserAndType(userId: Int, type: BurnType) {
        viewModelScope.launch {
            repository.getBurnsByUserAndType(userId, type) {
                burns.postValue(it)
            }
        }
    }

    fun createBurn(burn: Burn) {
        viewModelScope.launch {
            repository.createBurn(burn) { success, newBurn, error ->
                if (success) {
                    // Adiciona o novo burn à lista existente
                    val updatedBurns = burns.value?.toMutableList() ?: mutableListOf()
                    newBurn?.let { updatedBurns.add(it) }
                    burns.postValue(updatedBurns)
                    createBurnResult.postValue(Pair(true, null))
                } else {
                    createBurnResult.postValue(Pair(false, error))
                }
            }
        }
    }

    fun fetchLocationInfo(latitude: Double, longitude: Double) {
        RetrofitInstance.geoApiService.getLocationInfo(latitude, longitude).enqueue(object :
            Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                if (response.isSuccessful) {
                    locationInfo.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}