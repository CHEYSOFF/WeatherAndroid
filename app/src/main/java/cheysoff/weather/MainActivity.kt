package cheysoff.weather

import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import cheysoff.weather.parse.GetCoordinates
import cheysoff.weather.parse.GetWeather
import cheysoff.weather.ui.theme.LightPurple
import cheysoff.weather.ui.theme.MyTransparent
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
    Image(
        painter = painterResource(id = R.drawable.main_background),
        contentDescription = "background image",
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        contentScale = ContentScale.FillBounds
    )
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(LightPurple),
        shape = RoundedCornerShape(15.dp),


        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MyTransparent),

            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MyTransparent),
            ) {
                for (i in weatherList.indices) {
                    val pair = weatherList.get(i)
                    Row(
                        modifier = Modifier
                            .background(MyTransparent)
                    ) {
                        Text(text = "(${pair.first}, ${pair.second})")
                    }

                }
            }

        }
    }
//    Column {
//        for (i in weatherList.indices) {
//            val pair = weatherList.get(i)
//            Text(text = "(${pair.first}, ${pair.second})")
//        }
////        weatherList.forEach { pair ->
////            Text(text = "(${pair.first}, ${pair.second})")
////        }
//    }
}