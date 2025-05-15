package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.util.WeatherDataStore
import com.example.weatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WeatherScreen(applicationContext)
            }
        }
    }
}

@Composable
fun WeatherScreen(
    context: Context,
    viewModel: WeatherViewModel = viewModel()
) {
    var city by remember { mutableStateOf(TextFieldValue("")) }

    // ✅ Sử dụng LiveData theo cách Compose
    val weather by viewModel.weather.observeAsState()
    val forecast by viewModel.forecast.observeAsState()

    // ✅ Load cache khi khởi động
    LaunchedEffect(Unit) {
        val cached = WeatherDataStore.loadWeather(context)
        cached?.let { viewModel.restoreWeather(it) }
    }


    Column(
        Modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌤️ Dự báo thời tiết", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        BasicTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (city.text.isNotBlank()) {
                viewModel.fetchWeather(city.text, context)
                viewModel.fetchForecast(city.text)
            }
        }) {
            Text("Xem thời tiết")
        }

        Spacer(modifier = Modifier.height(24.dp))

        weather?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("🌆 ${it.name}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🌡️ Nhiệt độ: ${it.main.temp}°C", style = MaterialTheme.typography.bodyLarge)
                    Text("☁️ Mô tả: ${it.weather[0].description}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        val forecast by viewModel.forecast.observeAsState()

        forecast?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text("📅 Dự báo 5 ngày tới:", style = MaterialTheme.typography.titleMedium)

            it.forEach { item ->
                ForecastRow(item)
                Divider()
            }
        }
    }
}

@Composable
fun ForecastRow(item: ForecastItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.dt_txt.substring(0, 10),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${item.main.temp}°C",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Image(
                painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


