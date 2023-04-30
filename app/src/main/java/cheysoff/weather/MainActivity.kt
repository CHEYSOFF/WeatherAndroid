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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cheysoff.weather.data.RepositoriyImpl.ERROR_SIMPLE
import cheysoff.weather.domain.data.City
import cheysoff.weather.presention.State
import cheysoff.weather.presention.ViewModel
import cheysoff.weather.screens.MainViewModel
import cheysoff.weather.ui.theme.DarkBlue
import cheysoff.weather.ui.theme.LightBlue
import cheysoff.weather.ui.theme.LightBlue2
import cheysoff.weather.ui.theme.LightPurple
import cheysoff.weather.ui.theme.LightRed
import cheysoff.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.viewModelScope.launch {
            viewModel.screenState
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .buffer()
                .collect { state ->
                    when (state) {
                        is State.Start -> {
                            viewModel.getCoordinatesByCityName(CITY_NAME)
                        }

                        is State.HasCityData -> {
                            viewModel.getWeatherByCoordinates(state.city, DAYS)
                        }

                        is State.HasAllData -> {
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
        lifecycleScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) {
                withContext(Dispatchers.Main) {
                    setContent {
                        ShowError(ERROR_SIMPLE)
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

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = "Weather in ${city.name}",
                                color = White,
                                fontSize = 20.sp
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = "on ${city.latitude} ${city.longitude}",
                                color = White,
                                fontSize = 20.sp
                            )

                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = "for ${weatherList.size}",
                                color = White,
                                fontSize = 20.sp
                            )

                        }
                    }






                    for (i in weatherList.indices) {
                        val pair = weatherList[i]
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

                                    Text(
                                        buildAnnotatedString {
                                            withStyle(
                                                style = ParagraphStyle(lineHeight = 30.sp)

                                            ) {
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
                                        },
                                        fontSize = 30.sp
                                    )
                                }

                            }

                        }

                    }
                }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar() {

        WeatherTheme() {
            val viewModel = viewModel<MainViewModel>()
            val searchText by viewModel.searchText.collectAsState()
//                val persons by viewModel.persons.collectAsState()
            val isSearching by viewModel.isSearching.collectAsState()
            Column(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(1f)
//                        ) {
//                            items(persons) { person ->
//                                Text(
//                                    text = "${person.firstName} ${person.lastName}",
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 16.dp)
//                                )
//                            }
//                        }
                }
            }
        }
    }

    companion object {
        private const val CITY_NAME = "Moscow"
        private const val DAYS = 16
    }
}
