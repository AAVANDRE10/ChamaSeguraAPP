package com.example.chamasegura.data.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamasegura.data.entities.Municipality
import com.example.chamasegura.data.repository.MunicipalityRepository
import kotlinx.coroutines.launch

class MunicipalityViewModel : ViewModel() {

    private val repository = MunicipalityRepository()
    val municipalities: MutableLiveData<List<Municipality>> = MutableLiveData()

    fun getMunicipalities() {
        viewModelScope.launch {
            repository.getMunicipalities {
                municipalities.postValue(it)
            }
        }
    }

    fun createMunicipality(municipality: Municipality) {
        viewModelScope.launch {
            repository.createMunicipality(municipality) {
                // Atualize o estado conforme necessário
            }
        }
    }
}
