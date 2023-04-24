package cheysoff.weather.parse

import android.util.Log
import cheysoff.weather.City
import okhttp3.*
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Scanner

class GetCoordinates {
    fun coordinatesByCityName(cityName: String): City {
        val client = OkHttpClient()

        val url = "https://api.api-ninjas.com/v1/city?name=$cityName"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Api-Key", apiKey)
            .build()
        Log.d("HEY", cityName)

//        val response = client.newCall(request).execute()
        var response__ : Response
        var latitude = 0
        var longitude = 0
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Exception", "Message: ${e.message}")
                Log.d("FAIL", "")
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("RESPONSE", response.code.toString())
                Log.d("RESPONSE_HEADERS", response.headers.toString())
                val responseBodyString = response.body?.string()
                if (responseBodyString != null) {
                    Log.d("RESPONSE_BODY", responseBodyString)
                }

                response__ = response
                latitude =
                    getNextWordAfterTriggerWord(response.body?.byteStream(), "latitude")?.toIntOrNull()
                        ?: 0
                longitude =
                    getNextWordAfterTriggerWord(response.body?.byteStream(), "longitude")?.toIntOrNull()
                        ?: 0
            }
        })

        Log.d("HEY", latitude.toString() + longitude.toString())
        return City(cityName, latitude, longitude)
    }

    private fun getNextWordAfterTriggerWord(inputString: InputStream?, triggerWord: String): String? {
//        val byteInputStream = ByteArrayInputStream(inputString.toByteArray())
        val scanner = Scanner(inputString)
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

    private var apiKey = "/hnPSltbRJOd1gtTD/5tVQ==EiuCeIPf9So3R7TO"
}