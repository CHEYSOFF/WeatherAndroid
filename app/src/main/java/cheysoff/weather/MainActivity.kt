package cheysoff.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import cheysoff.weather.parse.GetCoordinates
import cheysoff.weather.parse.GetWeather
import cheysoff.weather.ui.theme.DarkBlue
import cheysoff.weather.ui.theme.LightBlue
import cheysoff.weather.ui.theme.LightPurple
import cheysoff.weather.ui.theme.LightRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

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


@OptIn(ExperimentalTextApi::class)
@Composable
fun WeatherList(weatherList: List<Pair<Double, Double>>) {
    Image(
        painter = painterResource(id = R.drawable.main_background),
        contentDescription = "background image",
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.7f),
        contentScale = ContentScale.FillBounds,
    )
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPurple,
        ),

        shape = RoundedCornerShape(15.dp),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 10.dp
//        )

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,

            ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center
            ) {
                for (i in weatherList.indices) {
                    val pair = weatherList[i]
                    Row(
//                        contentAlignment = Alignment.Center
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkBlue,
                            ),
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()

                            ) {

//                                val gradientColors = listOf(White, LightRed, White, LightBlue)
                                Text(
                                    buildAnnotatedString {
                                        withStyle(style = ParagraphStyle(lineHeight = 30.sp)) {
                                            withStyle(style = SpanStyle(color = White)) {
                                                append("from ")
                                            }
                                            withStyle(style = SpanStyle(color = LightBlue)) {
                                                append("${pair.first.roundToInt()} ºC")
                                            }
                                            withStyle(style = SpanStyle(color = White)) {
                                                append(" to ")
                                            }
                                            withStyle(style = SpanStyle(color = LightRed)) {
                                                append("${pair.second.roundToInt()} ºC")
                                            }
                                        }
                                    }
                                )
                            }

                        }

                    }

                }
            }

        }
    }
}