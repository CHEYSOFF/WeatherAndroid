package cheysoff.weather


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cheysoff.weather.adapterUI.NumberAdapter
import cheysoff.weather.parse.GetCoordinates
import cheysoff.weather.parse.GetWeather
//import kotlinx.android.synthetic.main.activity_main.numbersRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WOW", "it works no it doesnt")
        val lifecycleScope = lifecycleScope
        lifecycleScope.launch {
            val response = GetWeather().weatherByCoordinates(
                GetCoordinates().coordinatesByCityName("Moscow"),
                3
            )

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
//                val numbers = listOf(
//                    Pair(1.0, 2.0),
//                    Pair(3.0, 4.0),
//                    Pair(5.0, 6.0)
//                )
//                val numberAdapter = NumberAdapter(numbers)
//                numbersRecyclerView.adapter = numberAdapter
//                numbersRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

    }
}
