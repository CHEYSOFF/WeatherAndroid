package cheysoff.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import cheysoff.weather.data.RepositoriyImpl.ERROR_SIMPLE
import cheysoff.weather.domain.data.City
import cheysoff.weather.presention.State
import cheysoff.weather.presention.ViewModel
import cheysoff.weather.ui.theme.DarkBlue
import cheysoff.weather.ui.theme.LightBlue
import cheysoff.weather.ui.theme.LightBlue2
import cheysoff.weather.ui.theme.LightPurple
import cheysoff.weather.ui.theme.LightRed
import cheysoff.weather.ui.theme.Orange
import cheysoff.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) {
                withContext(Dispatchers.Main) {
                    setContent {
                        ShowError(ERROR_SIMPLE)
                    }
                }
            }
        }
        viewModel.viewModelScope.launch {
                viewModel.screenState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                    .buffer()
                    .collect { state ->
                        when (state) {
                            is State.Start -> {
                                Log.d("Start", "Start")

                                viewModel.getCoordinatesByCityName(CITY_NAME)
                            }

                            is State.HasCityData -> {
                                Log.d("HasCityData", "HasCityData")

                                viewModel.getWeatherByCoordinates(state.city, DAYS)
                            }

                            is State.HasAllData -> {
                                Log.d("HasAllData", "HasAllData")

                                withContext(Dispatchers.Main) {
                                    setContent {
                                        ShowWeatherList(state.weatherList, state.city)
                                    }
                                }
                            }

                            is State.Error -> {
                                setContent {
                                    ShowError(state.errorText)
                                }
                            }
                        }
                    }
            }



    }

    @Composable
    fun ShowError(errorText: String) {
        Text(
            text = errorText
        )
    }

    fun isInternetAvailable(): Boolean {
        try {
            val address = InetAddress.getByName("www.google.com")
            return !address.equals("")
        } catch (e: UnknownHostException) {
            Log.d("asqs", "e = ${e.message}")
        }
        return false
    }

    @Composable
    fun ShowWeatherList(weatherList: List<Pair<Double, Double>>, city: City) {
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

            ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,

                ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    SearchBar()
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = LightBlue2,
                        ),
                        shape = RoundedCornerShape(0.dp),


                        ) {
                        val textSize = 25
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White
                                        )
                                    ) {
                                        append("Weather in ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = Orange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(city.name)
                                    }
                                }
                            )

                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                buildAnnotatedString {

                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White
                                        )
                                    ) {
                                        append("on ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append("${city.latitude} ${city.longitude}")
                                    }


                                }
                            )

                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                buildAnnotatedString {

                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White
                                        )
                                    ) {
                                        append("for ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(weatherList.size.toString() + " ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = textSize.sp,
                                            color = White
                                        )
                                    ) {
                                        append("days")
                                    }


                                }
                            )

                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("d MMM")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkBlue,
                        ),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "DATE",
                                color = White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "FROM",
                                color = LightBlue,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "TO",
                                color = LightRed,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    for (i in weatherList.indices) {
                        val pair = weatherList[i]
                        val nextDate = dateFormat.format(calendar.time)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(5.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = DarkBlue,
                                ),
                                shape = RoundedCornerShape(100.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()

                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = nextDate,
                                            color = White,
                                            fontSize = 30.sp
                                        )
                                        Text(
                                            text = "${pair.first.roundToInt()} ºC",
                                            color = White,
                                            fontSize = 30.sp
                                        )
                                        Text(
                                            text = "${pair.second.roundToInt()} ºC",
                                            color = White,
                                            fontSize = 30.sp
                                        )
                                    }
                                }

                            }

                        }
                        calendar.add(Calendar.DATE, 1)
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun SearchBar() {

        WeatherTheme() {
            val searchText by viewModel.searchText.collectAsState()
            val isSearching by viewModel.isSearching.collectAsState()
            Column(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search by city name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            CITY_NAME = searchText

                            viewModel.setToStart()
                            Log.d("cn", CITY_NAME)
                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    companion object {
        var CITY_NAME = "Moscow"
        var DAYS = 16
    }
}
