package cheysoff.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import cheysoff.weather.parse.GetCoordinates
import cheysoff.weather.parse.GetWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lifecycleScope = lifecycleScope
        lifecycleScope.launch {
            val response = GetWeather().weatherByCoordinates(
                GetCoordinates().coordinatesByCityName("Moscow"),
                16
            )
            Log.d("weat", response.get(0).first.toString())
            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                setContent {
                    WeatherList(response)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun WeatherList(weatherList: List<Pair<Double, Double>>) {
    Column {
        for (i in weatherList.indices) {
            val pair = weatherList.get(i)
            Text(text = "(${pair.first}, ${pair.second})")
        }
//        weatherList.forEach { pair ->
//            Text(text = "(${pair.first}, ${pair.second})")
//        }
    }
}