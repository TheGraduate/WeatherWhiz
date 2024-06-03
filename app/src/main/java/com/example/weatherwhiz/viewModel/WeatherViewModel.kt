package com.example.weatherwhiz.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwhiz.api.NetworkService
import com.example.weatherwhiz.model.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    fun fetchWeather(lat: Double, lon: Double) {
        _isLoading.value = true
        _error.value = false

        viewModelScope.launch {
            try {
                val weatherData = NetworkService.weatherApi.getWeather(lat, lon)
                _weather.postValue(weatherData)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _error.postValue(true)
                _isLoading.postValue(false)
            }
        }
    }
}