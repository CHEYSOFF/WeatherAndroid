package cheysoff.weather


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import cheysoff.weather.parse.GetCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WOW", "it works no it doesnt")
        val lifecycleScope = lifecycleScope
        lifecycleScope.launch {
            // Perform some long-running task
            val response = GetCoordinates().coordinatesByCityName("Moscow")
            Log.d("City props", "${response.GetName()} ${response.GetLatitude()}  ${response.GetLongitude()}")
            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
//                updateUi()
            }
        }
    }
}
