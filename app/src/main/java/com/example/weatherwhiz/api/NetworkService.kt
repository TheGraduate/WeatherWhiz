package com.example.weatherwhiz.api

import com.example.weatherwhiz.util.CityDeserializer
import com.example.weatherwhiz.model.City
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val WEATHER_URL = "https://api.openweathermap.org/"
    private const val CITY_URL = "https://gist.githubusercontent.com/"
    const val WEATHER_API_KEY = "671006722ee1aae5cf04a0d0e2de2aef"

    private val gson = GsonBuilder()
        .registerTypeAdapter(City::class.java, CityDeserializer())
        .create()

    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    val cityApi: CityApi by lazy {
        Retrofit.Builder()
            .baseUrl(CITY_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CityApi::class.java)
    }
}