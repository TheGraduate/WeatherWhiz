package com.example.weatherwhiz.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwhiz.api.NetworkService
import com.example.weatherwhiz.model.City
import kotlinx.coroutines.launch

class CityListViewModel : ViewModel() {

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> get() = _cities

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    init {
        fetchCities()
    }

    fun fetchCities() {
        _isLoading.value = true
        _error.value = false

        viewModelScope.launch {
            try {
                val cityList = NetworkService.cityApi.getCities()
                val processedCityList = cityList.mapNotNull  { city ->
                    if (city != null && city.city.isNotBlank()) {
                        City(
                            id = city.id,
                            city = city.city,
                            latitude = if (city.latitude.isEmpty()) "0.0" else city.latitude,
                            longitude = if (city.longitude.isEmpty()) "0.0" else city.longitude
                        )
                    } else {
                        null
                    }
                }
                val sortedCityList: List<City?> = processedCityList.sortedBy { it?.city }
                _cities.postValue(sortedCityList as List<City>?)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                Log.e("CityListViewModel", "Error fetching cities", e)
                _error.postValue(true)
                _isLoading.postValue(false)
            }
        }
    }
}