package com.example.newyorkweather

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "03ea8df8f7b73af25f19b1e66750b7fd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Step 7: Retrofit instance with Moshi converter
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val service = retrofit.create(WeatherService::class.java)

        // Step 8: call the API
        service.getWeather("New York", apiKey).enqueue(object : Callback<WeatherResponse> {

            // Step 9: handle success
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val weather = body?.weather?.firstOrNull()
                    if (body != null && weather != null) {
                        findViewById<TextView>(R.id.cityText).text = body.name
                        findViewById<TextView>(R.id.mainText).text = weather.main
                        findViewById<TextView>(R.id.descriptionText).text = weather.description

                        val iconUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(findViewById(R.id.weatherIcon))
                    } else {
                        Log.e("Weather", "Empty or malformed body")
                    }
                } else {
                    Log.e("Weather", "Request failed: ${response.code()}")
                }
            }

            // Step 10: handle failure
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Weather", "Network or parsing failure", t)
            }
        })
    }
}