package cheysoff.weather


import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cheysoff.weather.parse.GetCoordinates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val myScope = CoroutineScope(Dispatchers.Main) // create a new CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("WOW", "it works no it doesnt")
        super.onCreate(savedInstanceState)
        Log.d("WOW", "it works no it doesnt")
        val lifecycleScope = lifecycleScope
        lifecycleScope.launch {
            // Perform some long-running task
            val response = GetCoordinates().coordinatesByCityName("Moscow")
            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
//                updateUi()
            }
        }
    }
}
