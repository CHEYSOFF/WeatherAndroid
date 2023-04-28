package cheysoff.weather.parse

import android.util.Log
import cheysoff.weather.City
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch

class GetWeather {
    fun weatherByCoordinates(city: City, days: Int): List<Pair<Double, Double>> {
        val minTemperaturesList = GetTemperatures(city, days, "min")
        val maxTemperaturesList = GetTemperatures(city, days, "max")
        return minTemperaturesList.zip(maxTemperaturesList)
    }

    fun GetTemperatures(city: City, days: Int, param: String): List<Double> {
        val client = OkHttpClient()
        val url =
            "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=${city.GetLatitude()}" +
                    "&longitude=${city.GetLongitude()}" +
                    "&timezone=auto" +
                    "&daily=temperature_2m_" +
                    "${param}&forecast_days=$days"
        val request = Request.Builder()
            .url(url)
            .build()
        val latch = CountDownLatch(1)
        var temperaturesList: List<Double>? = emptyList()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Exception", "Message: ${e.message}")
                Log.d("FAIL", "")
                latch.countDown()
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()

                temperaturesList = responseBodyString?.let {
                    TemperaturesFromText(
                        it,
                        "\"temperature_2m_$param\":[",
                        days
                    )
                }

                latch.countDown()
            }
        })
        latch.await()
        return temperaturesList ?: emptyList()
    }

    fun TemperaturesFromText(text: String, trigger: String, days: Int): List<Double> {
        val startIndex = text.indexOf(trigger) + trigger.length

        val temperatures = mutableListOf<Double>()
        val textStream = text.substring(startIndex).split("[,\\]]".toRegex()).toTypedArray()
        var daysPassed = 0
        for (token in textStream) {
            if(daysPassed >= days) {
                break
            }
            temperatures.add(token.toDouble())
            daysPassed++
        }

        return temperatures
    }
}