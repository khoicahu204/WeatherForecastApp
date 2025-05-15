package com.example.weatherapp.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.model.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weather_prefs")

object WeatherDataStore {
    private val WEATHER_KEY = stringPreferencesKey("weather_data")

    suspend fun saveWeather(context: Context, weather: WeatherResponse) {
        val json = Gson().toJson(weather)
        context.dataStore.edit { prefs ->
            prefs[WEATHER_KEY] = json
        }
    }

    suspend fun loadWeather(context: Context): WeatherResponse? {
        val prefs = context.dataStore.data.first()
        val json = prefs[WEATHER_KEY]
        return json?.let {
            Gson().fromJson(it, WeatherResponse::class.java)
        }
    }
}