package cheysoff.weather.parse

import android.util.Log
import cheysoff.weather.City
import okhttp3.*
import java.io.IOException
import java.net.ConnectException
import java.util.Scanner
import java.util.concurrent.CountDownLatch

class GetCoordinates {
    fun coordinatesByCityName(cityName: String): City {
        val client = OkHttpClient()

        val url =
            "https://nominatim.openstreetmap.org/search?city=$cityName&countrycodes=\$countryCode&limit=9&format=json"
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("CityName", cityName)
        var latitude = 0.0
        var longitude = 0.0
        val latch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(e is ConnectException) {
                    Log.d("Fail", "No internet!")
                }
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()
                val latitudeString = getNextWordAfterTriggerWord(responseBodyString, "\"lat\"")
                latitude = latitudeString?.substring(1, latitudeString.length - 1)?.toDoubleOrNull()
                    ?: 0.0

                val longitudeString = getNextWordAfterTriggerWord(responseBodyString, "\"lon\"")
                longitude =
                    longitudeString?.substring(1, longitudeString.length - 1)?.toDoubleOrNull()
                        ?: 0.0

                latch.countDown()
            }
        })
        latch.await()
        Log.d("Lat and Long", latitude.toString() + longitude.toString())
        return City(cityName, latitude, longitude)
    }

    private fun getNextWordAfterTriggerWord(inputString: String?, triggerWord: String): String? {
        val scanner = Scanner(inputString).useDelimiter("[,:]")
        var shouldReturnNextWord = false
        var nextWord: String? = null

        while (scanner.hasNext()) {
            val word = scanner.next()
            if (shouldReturnNextWord) {
                nextWord = word
                break
            }

            if (word == triggerWord) {
                shouldReturnNextWord = true
            }
        }

        return nextWord
    }

}