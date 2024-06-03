package com.example.weatherwhiz.util

import com.example.weatherwhiz.model.City
import com.google.gson.*
import java.lang.reflect.Type

class CityDeserializer : JsonDeserializer<City> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): City {
        val jsonObject = json?.asJsonObject
        val id = jsonObject?.get("id")?.asString ?: ""
        val city = jsonObject?.get("city")?.asString ?: ""
        val latitude = jsonObject?.get("latitude")?.asString?.takeIf { it.isNotEmpty() } ?: "0.0"
        val longitude = jsonObject?.get("longitude")?.asString?.takeIf { it.isNotEmpty() } ?: "0.0"

        return City(id, city, latitude, longitude)
    }
}